import { create } from 'zustand';
import liff from '@line/liff';
import { authService } from '../services/authService';

// 定義 User 類型 (範例)
interface UserProfile {
    userId: string;
    displayName: string;
    pictureUrl?: string;
}

// 定義 Store 的狀態 (State)
interface AuthState {
    // 修正: 我們不再需要將 liff 儲存在 state 中，
    // 因為它現在是從 import 全局引入的。
    // 我們改為追蹤 "LIFF 是否已初始化"
    isLiffInitialized: boolean;
    liffError: string | null;
    jwt: string | null;
    lineProfile: UserProfile | null;
    isAuthenticated: boolean;
    isLoading: boolean;
}

// 定義 Store 的動作 (Actions)
interface AuthActions {
    initializeLiff: (liffId: string) => Promise<void>;
    login: () => void;
    logout: () => void;
    handleBackendLogin: () => Promise<void>;
    checkAuthStatus: () => void;
}

// 建立 Store
export const useAuthStore = create<AuthState & AuthActions>((set, get) => ({
    // --- 初始狀態 ---
    isLiffInitialized: false, // 修正
    liffError: null,
    jwt: localStorage.getItem('jwt') || null,
    lineProfile: null,
    isAuthenticated: !!localStorage.getItem('jwt'),
    isLoading: false,

    // --- 動作 (Actions) ---

    /**
     * 1. 初始化 LIFF SDK
     */
    initializeLiff: async (liffId) => {
        try {
            set({ isLoading: true });
            // 修正: 直接使用全局 import 的 'liff'
            await liff.init({ liffId: liffId });

            set({
                isLiffInitialized: true, // 標記為已初始化
                liffError: null
            });
        } catch (error: any) {
            console.error('LIFF initialization failed', error);
            set({ isLiffInitialized: false, liffError: error.toString() });
        } finally {
            set({ isLoading: false });
        }
    },

    /**
     * 2. 檢查當前認證狀態
     */
    checkAuthStatus: () => {
        // 修正: 檢查 flag
        if (!get().isLiffInitialized) return;
        const jwt = get().jwt;

        // 修正: 直接使用全局 'liff'
        if (liff.isLoggedIn() && jwt) {
            // 狀態已同步
            set({ isAuthenticated: true });
            liff.getProfile().then(profile => set({ lineProfile: profile }));
        }
        // 情況 B: 已登入 Line 但沒有 JWT (例如 JWT 過期)
        else if (liff.isLoggedIn() && !jwt) {
            get().handleBackendLogin();
        }
        // 情況 C: 未登入 Line
        else {
            get().logout();
        }
    },

    /**
     * 3. 觸發 Line 登入 (Web 或 LIFF)
     */
    login: () => {
        // 修正: 直接使用全局 'liff'
        if (get().isLiffInitialized) {
            liff.login();
        }
    },

    /**
     * 4. 處理後端登入 (用 Line Token 換 JWT)
     */
    handleBackendLogin: async () => {
        // 修正: 檢查 flag 並直接使用全局 'liff'
        if (!get().isLiffInitialized || !liff.isLoggedIn()) {
            return; // 未登入 Line
        }

        set({ isLoading: true });
        try {
            // 取得 Line Access Token
            const lineAccessToken = liff.getAccessToken();
            if (!lineAccessToken) {
                throw new Error('Failed to get Line Access Token');
            }

            // 呼叫後端 API
            const response = await authService.lineLogin(lineAccessToken);
            const jwt = response.data.accessToken;

            // 儲存 JWT
            localStorage.setItem('jwt', jwt);

            // 更新狀態
            const profile = await liff.getProfile();
            set({
                jwt: jwt,
                isAuthenticated: true,
                lineProfile: profile,
            });

        } catch (error) {
            console.error('Backend login failed', error);
            get().logout(); // 如果後端登入失敗，則強制登出
        } finally {
            set({ isLoading: false });
        }
    },

    /**
     * 5. 登出
     */
    logout: () => {
        // 修正: 直接使用全局 'liff'
        if (get().isLiffInitialized && liff.isLoggedIn()) {
            liff.logout();
        }
        localStorage.removeItem('jwt');
        set({
            jwt: null,
            isAuthenticated: false,
            lineProfile: null,
        });
    },
}));