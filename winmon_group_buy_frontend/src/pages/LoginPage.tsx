// src/pages/LoginPage.tsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { liffService } from '../services/liffService';
import { loginWithLiffToken, redirectToLineLogin } from '../api/authService';
import { Box, CircularProgress, Typography, Container, Alert } from '@mui/material';

const LoginPage: React.FC = () => {
    const auth = useAuth();
    const navigate = useNavigate();
    const [loadingMessage, setLoadingMessage] = useState('正在初始化登入服務...');
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        // 流程 1: 檢查是否已有 JWT Token
        if (auth.token) {
            navigate('/home');
            return;
        }

        const loginFlow = async () => {
            try {
                // 流程 2: 初始化 LIFF
                setLoadingMessage('正在初始化 LIFF...');
                await liffService.init();

                if (liffService.status.error) {
                    throw new Error(liffService.status.error);
                }

                // 流程 3: 檢查 LIFF 是否已登入 (liff.isLoggedIn())
                if (liffService.status.isLoggedIn) {
                    setLoadingMessage('偵測到 LIFF 登入，正在驗證...');
                    const liffToken = liffService.getAccessToken();

                    if (liffToken) {
                        // 將 LIFF Access Token 送至後端交換 JWT
                        const data = await loginWithLiffToken(liffToken);
                        auth.login(data.token); // 儲存 JWT (步驟 5)
                        navigate('/home'); // 轉至首頁 (步驟 5)
                    } else {
                        throw new Error('無法取得 LIFF Access Token');
                    }
                } else {
                    // 流程 4: LIFF 未登入，判斷環境
                    if (liffService.status.isInClient) {
                        // 在 LIFF App 內 -> 呼叫 liff.login()
                        setLoadingMessage('轉跳至 LIFF 登入...');
                        liffService.login(); // 頁面將會刷新或導轉
                    } else {
                        // 在電腦瀏覽器 -> 導向 Line Login 頁面
                        setLoadingMessage('轉跳至 Line 登入...');
                        redirectToLineLogin(); // (導向 access.line.me)
                    }
                }
            } catch (err: any) {
                console.error('Login Flow Error:', err);
                setError(`登入失敗: ${err.message}. 請稍後再試。`);
            }
        };

        // 只有在 AuthContext 載入完畢且確定沒有 token 時才執行
        if (!auth.isLoading && !auth.token) {
            loginFlow();
        }

    }, [auth.token, auth.isLoading, navigate, auth]);

    // 登入頁面通常只顯示一個載入畫面
    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    minHeight: '60vh',
                }}
            >
                <CircularProgress />
                <Typography component="h1" variant="h6" sx={{ mt: 3 }}>
                    {loadingMessage}
                </Typography>
                {error && (
                    <Alert severity="error" sx={{ mt: 2, width: '100%' }}>
                        {error}
                    </Alert>
                )}
            </Box>
        </Container>
    );
};

export default LoginPage;