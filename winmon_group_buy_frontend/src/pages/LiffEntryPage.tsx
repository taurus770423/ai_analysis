import React, { useEffect, useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { getLiffAccessToken, initializeLiff } from '../services/liffService';
import { apiClient } from '../services/api';
import LoadingSpinner from '../components/common/LoadingSpinner';

const LiffEntryPage: React.FC = () => {
    const { user, loginWithToken, isLoading: isAuthLoading } = useAuth();
    const [liffError, setLiffError] = useState<string | null>(null);
    const [isLiffInited, setIsLiffInited] = useState(false);

    useEffect(() => {
        const liffLoginFlow = async () => {
            try {
                // 1. 初始化 LIFF 並確保登入
                await initializeLiff();
                setIsLiffInited(true);

                // 2. 取得 Line Access Token
                const lineAccessToken = getLiffAccessToken();
                if (!lineAccessToken) {
                    throw new Error('無法取得 Line Access Token');
                }

                // 3. 呼叫後端的 /auth/line/liff-login API
                // (這會自動使用 'api.ts' 中的 apiClient，所以不需要手動加 token)
                const response = await apiClient.post<{ jwt: string }>('/auth/line/liff-login', {
                    lineAccessToken: lineAccessToken,
                });

                const backendJwt = response.data.jwt;
                if (!backendJwt) {
                    throw new Error('後端未回傳 JWT');
                }

                // 4. 使用後端 JWT 登入我們自己的系統
                await loginWithToken(backendJwt);

            } catch (error: any) {
                console.error('LIFF 登入流程失敗:', error);
                setLiffError(error.message || 'LIFF 登入失敗');
            }
        };

        // 只有在尚未登入時才執行 LIFF 流程
        // (避免 initializeAuth 已經登入後又重複執行)
        if (!user && !isAuthLoading) {
            liffLoginFlow();
        }
    }, [user, loginWithToken, isAuthLoading]);

    // 渲染邏輯
    if (liffError) {
        return (
            <div>
                <h2>LIFF 錯誤</h2>
                <p>{liffError}</p>
            </div>
        );
    }

    // 顯示正在登入的狀態
    // isAuthLoading 是 loginWithToken 觸發的
    // !isLiffInited 是 liff.init() 尚未完成
    if (isAuthLoading || !isLiffInited || !user) {
        return <LoadingSpinner />;
    }

    // 登入成功！
    return (
        <div style={{ padding: '1rem' }}>
            <h2>歡迎, {user.displayName} (LIFF)</h2>
            <p>這是在 LIFF 視窗中顯示的團購頁面。</p>
            {/* 這裡可以開始放 LIFF 專用的商品列表、購物車等...
        注意：LIFF 頁面通常不使用 AppLayout，而是有自己的介面
      */}
            {user.pictureUrl && (
                <img
                    src={user.pictureUrl}
                    alt="Profile"
                    style={{ width: 80, height: 80, borderRadius: '50%' }}
                />
            )}
        </div>
    );
};

export default LiffEntryPage;