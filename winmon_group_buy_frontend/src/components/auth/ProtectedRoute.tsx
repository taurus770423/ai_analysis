import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import LoadingSpinner from '../common/LoadingSpinner';

const ProtectedRoute: React.FC = () => {
    const { user, isLoading } = useAuth();

    if (isLoading) {
        return <LoadingSpinner />;
    }

    if (!user) {
        // 如果沒登入，導向到登入頁
        // (這裡我們假設 /login 是一般 Web 登入頁)
        return <Navigate to="/login" replace />;
    }

    // 已登入，顯示子路由的內容
    return <Outlet />;
};

export default ProtectedRoute;