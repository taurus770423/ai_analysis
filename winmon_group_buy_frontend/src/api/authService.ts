// src/api/authService.ts
import apiClient from './apiClient';
import { LINE_LOGIN_CLIENT_ID, LINE_LOGIN_CALLBACK_URL } from '../constants/common.ts';

// 處理 LIFF 登入 (使用 LIFF Access Token)
export const loginWithLiffToken = async (accessToken: string) => {
    // 後端端點: /auth/line/login
    // 注意：後端需要能同時處理 'accessToken' (來自 LIFF)
    const response = await apiClient.post('/auth/line/login', { accessToken });
    return response.data; // 應包含 { token: '...' }
};

// 處理電腦版登入 (使用 authorization_code)
export const loginWithCode = async (code: string) => {
    // 後端端點: /auth/line/login
    // 注意：後端需要能同時處理 'code' (來自 Web Login)
    const response = await apiClient.post('/auth/line/login', { code });
    return response.data; // 應包含 { token: '...' }
};

// 產生一個隨機 state 用於 CSRF 防護
const generateState = () => {
    const random = Math.random().toString(36).substring(7);
    sessionStorage.setItem('lineLoginState', random);
    return random;
};

// 導向至 Line Login (電腦版)
export const redirectToLineLogin = () => {
    const state = generateState();
    const scope = 'profile openid email'; // 根據您的需求調整

    const queryParams = new URLSearchParams({
        response_type: 'code',
        client_id: LINE_LOGIN_CLIENT_ID,
        redirect_uri: LINE_LOGIN_CALLBACK_URL,
        state: state,
        scope: scope,
    });

    const loginUrl = `https://access.line.me/oauth2/v2.1/authorize?${queryParams.toString()}`;

    window.location.href = loginUrl;
};