// src/services/api.ts
import axios from 'axios';
import { useAuthStore } from '../store/authStore';
import config from '../config';

// 建立 Axios 實例
const api = axios.create({
    // baseURL 在 Vite proxy 中已設定，這裡設為 '/'
    // 我們的請求會是 /group-buy-dev/api/...
    baseURL: '/',
    headers: {
        'Content-Type': 'application/json',
    },
});

// 請求攔截器 (Request Interceptor)
// 在 *每次* 請求發送前，自動帶上 JWT
api.interceptors.request.use(
    (config) => {
        const jwt = useAuthStore.getState().jwt;
        if (jwt) {
            config.headers['Authorization'] = `Bearer ${jwt}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 回應攔截器 (Response Interceptor)
// 處理 401 (未授權)
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response && error.response.status === 401) {
            // JWT 過期或無效
            console.warn('JWT expired or invalid. Logging out.');
            useAuthStore.getState().logout();
            // 重新整理頁面到登入頁
            window.location.href = config.basePath + '/login';
        }
        return Promise.reject(error);
    }
);

export default api;