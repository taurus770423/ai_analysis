import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const AuthCallbackPage: React.FC = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { loginWithToken } = useAuth();
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const handleLogin = async () => {
            // 1. 從 URL 取得後端傳來的 token
            const token = searchParams.get('token');

            if (token) {
                try {
                    // 2. 使用這個 token 進行登入 (會存 localSotrage 並取得 /user/me)
                    await loginWithToken(token);
                    // 3. 登入成功，導向到個人資料頁
                    navigate('/profile', { replace: true });
                } catch (err) {
                    setError('登入失敗，無法驗證 Token。');
                }
            } else {
                setError('登入失敗，未收到 Token。');
                // (可以選擇導回登入頁)
                // navigate('/login', { replace: true });
            }
        };

        handleLogin();
    }, [searchParams, loginWithToken, navigate]);

    if (error) {
        return (
            <div>
                <h2>登入錯誤</h2>
                <p>{error}</p>
            </div>
        );
    }

    return (
        <div>
            <h2>正在登入中...</h2>
            <p>請稍候，正在為您處理登入...</p>
        </div>
    );
};

export default AuthCallbackPage;