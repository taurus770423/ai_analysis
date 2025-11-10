import liff from '@line/liff';
import { environment } from '../utils/environment';

export const initializeLiff = async (): Promise<void> => {
    try {
        if (!environment.liffId) {
            throw new Error('LIFF ID not configured in .env');
        }

        await liff.init({ liffId: environment.liffId });

        // 檢查是否在 Line App 外部
        if (!liff.isInClient()) {
            // 可以在這裡決定是否要導向回 Line (非必要)
            console.warn('Running outside of LINE client.');
        }

        // 檢查是否登入
        if (!liff.isLoggedIn()) {
            // 如果沒登入，自動導向 Line 登入頁
            // (登入後會自動回來此頁面)
            liff.login();
        }
        // 如果已登入，liff.init() 會完成
    } catch (error) {
        console.error('LIFF initialization failed', error);
        throw error;
    }
};

export const getLiffAccessToken = (): string | null => {
    if (!liff.isLoggedIn()) {
        return null;
    }
    return liff.getAccessToken();
};