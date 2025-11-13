// src/routes/ProtectedRoute.tsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Box, CircularProgress } from '@mui/material';

export const ProtectedRoute: React.FC<{ children: React.ReactElement }> = ({ children }) => {
    const auth = useAuth();

    if (auth.isLoading) {
        // 等待 AuthContext 檢查 sessionStorage
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    if (!auth.token) {
        // 步驟 2: 如果 JWT 不存在，導回登入頁
        return <Navigate to="/" replace />;
    }

    return children;
};