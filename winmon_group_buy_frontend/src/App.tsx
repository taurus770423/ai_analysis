// src/App.tsx
import { useEffect } from 'react';
import { RouterProvider } from 'react-router-dom';
import { router } from './router';
import { useAuthStore } from './store/authStore';
import config from './config';
import LoadingSpinner from './components/ui/LoadingSpinner';

function App() {
    const { initializeLiff, liff, checkAuthStatus, isLoading } = useAuthStore();

    // 1. App 啟動時，初始化 LIFF
    useEffect(() => {
        if (config.liffId) {
            initializeLiff(config.liffId);
        }
    }, [initializeLiff]);

    // 2. 當 LIFF 初始化完成後，檢查登入狀態
    useEffect(() => {
        if (liff) {
            checkAuthStatus();
        }
    }, [liff, checkAuthStatus]);

    // 在 LIFF 初始化期間顯示全局加載
    if (isLoading && !liff) {
        return <LoadingSpinner fullScreen={true} />
    }

    return <RouterProvider router={router} />;
}

export default App;