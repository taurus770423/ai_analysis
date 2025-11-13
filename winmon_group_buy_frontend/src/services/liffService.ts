// src/services/liffService.ts
import liff from '@line/liff';
import { LIFF_ID } from '../constants/common.ts';

interface LiffStatus {
    isInitialized: boolean;
    isInClient: boolean;
    isLoggedIn: boolean;
    error?: string;
}

class LiffService {
    public status: LiffStatus = {
        isInitialized: false,
        isInClient: false,
        isLoggedIn: false,
    };

    async init(): Promise<void> {
        try {
            await liff.init({ liffId: LIFF_ID });
            this.status.isInitialized = true;
            this.status.isInClient = liff.isInClient();
            this.status.isLoggedIn = liff.isLoggedIn();
        } catch (error: any) {
            console.error('LIFF Init Error:', error);
            this.status.error = error.message;
        }
    }

    getAccessToken(): string | null {
        if (!this.status.isLoggedIn) {
            return null;
        }
        return liff.getAccessToken();
    }

    login(): void {
        if (!this.status.isInitialized) return;
        // liff.login() 會自動處理 callback，
        // 並在登入後將使用者導回同一頁面
        liff.login();
    }

    logout(): void {
        if (!this.status.isLoggedIn) return;
        liff.logout();
        window.location.reload();
    }
}

export const liffService = new LiffService();