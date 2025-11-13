// src/routes/AppRoutes.tsx
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '../pages/LoginPage';
import AuthCallbackPage from '../pages/AuthCallbackPage';
import HomePage from '../pages/HomePage';
import { ProtectedRoute } from './ProtectedRoute';

const AppRoutes: React.FC = () => {
    return (
        <BrowserRouter basename="/group-buy-dev"> {/* Nginx 轉發的基礎路徑 */}
            <Routes>
                {/* 步驟 1: 訪問 / 進入登入頁 */}
                <Route path="/" element={<LoginPage />} />

                {/* 電腦版登入的回呼路徑 */}
                <Route path="/auth/callback" element={<AuthCallbackPage />} />

                {/* 步驟 5: 登入後轉至首頁 */}
                <Route
                    path="/home"
                    element={
                        <ProtectedRoute>
                            <HomePage />
                        </ProtectedRoute>
                    }
                />

                {/* 其他路徑都導回登入頁 */}
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </BrowserRouter>
    );
};

export default AppRoutes;