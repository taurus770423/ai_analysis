import React from 'react';
import { useAuth } from '../hooks/useAuth';
import { Navigate } from 'react-router-dom';

const LoginPage: React.FC = () => {
    const { user, isLoading, webLoginUrl } = useAuth();

    // 如果正在載入或已經登入，就導向到個人資料頁
    if (isLoading) return <div>Loading...</div>;
    if (user) {
        return <Navigate to="/profile" replace />;
    }

    return (
        <div>
            <h2>登入</h2>
            <p>請點擊下方按鈕以 Line 帳號登入：</p>
            {/* 這個連結會導向到後端 Spring Security 的 /oauth2/authorization/line
        後端會處理所有 OAuth 流程，最後帶著 token 重導向回 /auth/callback
      */}
            <a href={webLoginUrl} style={{ padding: '10px 20px', background: '#00C300', color: 'white', textDecoration: 'none' }}>
                使用 Line 登入 (Web)
            </a>
        </div>
    );
};

export default LoginPage;