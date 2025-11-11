// src/services/authService.ts
import api from './api';
import config from '../config';

// 後端登入 API 的 Response 類型 (與後端 LoginResponse.java 對應)
interface LoginResponse {
    accessToken: string;
    tokenType: string;
}

export const authService = {
    /**
     * 呼叫後端 /auth/line/login
     * @param lineAccessToken 從 LIFF 取得的 Token
     */
    lineLogin: (lineAccessToken: string) => {
        // 完整的請求 URL 會是: /group-buy-dev/api/auth/line/login
        const url = `${config.apiBasePath}/auth/line/login`;
        return api.post<LoginResponse>(url, { lineAccessToken });
    },

    // 範例：獲取受保護的用戶資訊
    getProtectedData: () => {
        const url = `${config.apiBasePath}/products`; // 假設 /products 是受保護的
        return api.get(url);
    }
};