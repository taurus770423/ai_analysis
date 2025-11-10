import axios from 'axios';
import { environment } from '../utils/environment';

const TOKEN_KEY = 'groupbuy_jwt';

export const apiClient = axios.create({
    baseURL: environment.backendBaseUrl,
    headers: {
        'Content-Type': 'application/json',
    },
});

// 請求攔截器：在每個請求送出前，自動加上 Authorization header
apiClient.interceptors.request.use(
    (config) => {
        const token = getJwtToken();
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// --- Token 輔助函式 ---

export const setJwtToken = (token: string) => {
    localStorage.setItem(TOKEN_KEY, token);
};

export const getJwtToken = (): string | null => {
    return localStorage.getItem(TOKEN_KEY);
};

export const removeJwtToken = () => {
    localStorage.removeItem(TOKEN_KEY);
};