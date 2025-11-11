// src/router/index.tsx
import { createBrowserRouter } from 'react-router-dom';
import config from '../config';
import HomePage from '../pages/HomePage';
import LoginPage from '../pages/LoginPage';
import ProductsPage from '../pages/ProductsPage';
import ProtectedRoute from '../components/router/ProtectedRoute';
import MainLayout from '../components/layout/MainLayout.tsx';

export const router = createBrowserRouter(
    [
        {
            path: '/login',
            element: <LoginPage />,
        },
        {
            path: '/',
            // 使用路由守衛
            element: (
                <ProtectedRoute>
                    <MainLayout />
                </ProtectedRoute>
            ),
            // 受保護的子路由
            children: [
                {
                    index: true, // 預設子路由 (即 '/')
                    element: <HomePage />,
                },
                {
                    path: 'products',
                    element: <ProductsPage />,
                },
                // ... 其他團購、訂單頁面
            ],
        },
    ],
    {
        // !!! 關鍵：設定基礎路徑，對應 Nginx 和 Vite
        basename: config.basePath,
    }
);