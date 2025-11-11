// src/App.tsx
import { useEffect } from 'react';
import { RouterProvider } from 'react-router-dom';
import { router } from './router';
import { useAuthStore } from './store/authStore';
import config from './config';
import LoadingSpinner from './components/ui/LoadingSpinner';

function App() {
    // --- 修正 1: 移除 liff，改用 isLiffInitialized ---
    const { initializeLiff, isLiffInitialized, checkAuthStatus, isLoading } = useAuthStore();

    // 1. App 啟動時，初始化 LIFF
    useEffect(() => {
        if (config.liffId) {
            initializeLiff(config.liffId);
        }
    }, [initializeLiff]);

    // --- 修正 2: 依賴從 liff 改為 isLiffInitialized ---
    useEffect(() => {
        if (isLiffInitialized) {
            checkAuthStatus();
        }
    }, [isLiffInitialized, checkAuthStatus]);

    // --- 修正 3: 判斷條件改為 !isLiffInitialized ---
    // (在 LIFF 尚未初始化完成前，都顯示 Loading)
    if (isLoading && !isLiffInitialized) {
        return <LoadingSpinner fullScreen={true} />
    }

    return <RouterProvider router={router} />;
}

export default App;