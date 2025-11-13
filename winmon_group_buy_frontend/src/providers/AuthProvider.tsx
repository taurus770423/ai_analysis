// src/providers/AuthProvider.tsx
import React, { useState, useEffect, type ReactNode, useMemo } from 'react';
import { AuthContext, type AuthContextType } from '../contexts/AuthContext'; // 引入類型和 Context
import apiClient from '../api/apiClient';

// 這是您原本在 AuthContext.tsx 中的組件邏輯
export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        // 應用程式啟動時，檢查 sessionStorage
        const storedToken = sessionStorage.getItem('jwtToken');
        if (storedToken) {
            setToken(storedToken);
            // 將 token 設置到 axios 預設標頭
            apiClient.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
        }
        setIsLoading(false);
    }, []);

    const login = (newToken: string) => {
        setToken(newToken);
        sessionStorage.setItem('jwtToken', newToken);
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
    };

    const logout = () => {
        setToken(null);
        sessionStorage.removeItem('jwtToken');
        delete apiClient.defaults.headers.common['Authorization'];
        // 導回登入頁的邏輯應由 ProtectedRoute 或登出按鈕處理
    };

    // 使用 useMemo 確保 value object 只在 token 或 isLoading 變化時才重新生成
    const value: AuthContextType = useMemo(
        () => ({ token, isLoading, login, logout }),
        [token, isLoading]
    );

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};