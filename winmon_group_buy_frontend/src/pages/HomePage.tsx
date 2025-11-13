// src/pages/HomePage.tsx
import React from 'react';
import { useAuth } from '../hooks/useAuth';
import { liffService } from '../services/liffService';
import { Box, Button, Typography, Container } from '@mui/material';

const HomePage: React.FC = () => {
    const auth = useAuth();

    const handleLogout = () => {
        // 先登出 LIFF (如果在 LIFF 內)
        if (liffService.status.isInClient && liffService.status.isLoggedIn) {
            liffService.logout(); // LIFF 登出會刷新頁面
        }
        // 再清除 App 的 JWT
        auth.logout();
        // AuthProvider 會處理導向，或者 ProtectedRoute 會在下次訪問時攔截
        window.location.href = '/group-buy-dev/'; // 強制導回登入頁
    };

    return (
        <Container component="main" maxWidth="md">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Typography component="h1" variant="h4">
                    歡迎！
                </Typography>
                <Typography component="p" sx={{ mt: 2 }}>
                    您已成功登入。
                </Typography>
                <Button
                    variant="contained"
                    color="secondary"
                    onClick={handleLogout}
                    sx={{ mt: 4 }}
                >
                    登出
                </Button>
            </Box>
        </Container>
    );
};

export default HomePage;