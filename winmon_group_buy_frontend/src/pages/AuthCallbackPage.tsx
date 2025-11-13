// src/pages/AuthCallbackPage.tsx
import React, { useEffect, useState, useRef } from 'react'; // <-- 確保引入 useRef
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { loginWithCode, redirectToLineLogin } from '../api/authService';
import { Box, CircularProgress, Typography, Container, Alert, Button } from '@mui/material';

const AuthCallbackPage: React.FC = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const auth = useAuth();

    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    // *** 解決方案：使用 ref 來防止 Strict Mode 執行兩次 ***
    const isProcessing = useRef(false);

    useEffect(() => {
        // *** 守衛：如果正在處理中，就直接返回，不再執行第二次 ***
        if (isProcessing.current) {
            return;
        }
        isProcessing.current = true; // 標記為 "處理中"

        const handleCallback = async () => {
            // 如果已經登入，就不用再跑一次了
            if (auth.token) {
                navigate('/home');
                return;
            }

            const code = searchParams.get('code');
            const state = searchParams.get('state');
            const storedState = sessionStorage.getItem('lineLoginState');

            // 1. 檢查 CSRF State
            if (state !== storedState) {
                setError('登入驗證失敗 (State Mismatch)，請重新登入。');
                setIsLoading(false); // 停止 loading
                sessionStorage.removeItem('lineLoginState'); // 清除
                return; // *** 停止執行 ***
            }
            sessionStorage.removeItem('lineLoginState'); // 驗證完畢，清除 state

            // 2. 檢查 Line 是否回傳 code
            if (code) {
                try {
                    // 步驟 3: 將 code 傳送給後端
                    const data = await loginWithCode(code);

                    // 步驟 4 & 5: 驗證成功
                    auth.login(data.token);
                    navigate('/home'); // 成功，轉跳到首頁
                } catch (err: any) {
                    // 步驟 3.5: 後端 API 驗證失敗
                    console.error('Callback API Error:', err);
                    setError(`驗證失敗: ${err.message}. 請稍後再試。`);
                    setIsLoading(false); // 停止 loading
                }
            } else {
                // 3. Line 回傳錯誤 (例如使用者按了「取消」)
                const errorParam = searchParams.get('error');
                const errorDesc = searchParams.get('error_description');
                setError(`Line 登入失敗: ${errorParam} (${errorDesc || '使用者取消了操作'})`);
                setIsLoading(false); // 停止 loading
            }
        };

        handleCallback();
    }, [searchParams, navigate, auth]); // 依賴項保持不變

    // 手動重試登入的 Function
    const handleRetryLogin = () => {
        setIsLoading(true); // 顯示 loading
        setError(null);     // 清除錯誤
        // 注意：這裡不需要重置 isProcessing.current
        // 因為 redirectToLineLogin() 會離開當前頁面
        // 當使用者回來時，這會是一個全新的頁面加載，ref 會自動重置為 false
        redirectToLineLogin(); // 呼叫和 LoginPage 一樣的轉跳
    };

    // ... (JSX 保持不變) ...
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
                {/* 狀態 1: 正在載入 */}
                {isLoading && (
                    <>
                        <CircularProgress />
                        <Typography component="h1" variant="h6" sx={{ mt: 3 }}>
                            正在驗證您的登入...
                        </Typography>
                    </>
                )}

                {/* 狀態 2: 發生錯誤 */}
                {!isLoading && error && (
                    <>
                        <Typography component="h1" variant="h5" color="error">
                            登入失敗
                        </Typography>
                        <Alert severity="error" sx={{ mt: 2, width: '100%' }}>
                            {error}
                        </Alert>
                        <Button
                            fullWidth
                            variant="contained"
                            onClick={handleRetryLogin}
                            sx={{ mt: 3, mb: 2 }}
                        >
                            重新登入
                        </Button>
                    </>
                )}
            </Box>
        </Container>
    );
};

export default AuthCallbackPage;