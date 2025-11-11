// src/pages/LoginPage.tsx
import { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import liff from '@line/liff';
import { useAuthStore } from '../store/authStore';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import { Button } from '../components/ui/Button';

const LoginPage = () => {
    const {
        liffError,
        isAuthenticated,
        login,
        handleBackendLogin,
        isLoading
    } = useAuthStore();

    useEffect(() => {
        // 當 LIFF 初始化完成後...
        if (liff && !isAuthenticated) {
            // 情況 1: 用戶已登入 Line (例如剛從 Line 登入頁跳轉回來)
            // 但尚未登入我們的後端 (沒有 JWT)
            if (liff.isLoggedIn()) {
                console.log('User is logged in to Line. Attempting backend login...');
                handleBackendLogin();
            }
            // 情況 2: 用戶未登入 Line
            // 等待用戶點擊登入按鈕
        }
    }, [liff, isAuthenticated, handleBackendLogin]);

    // 如果後端登入成功 (isAuthenticated 變為 true)，則導向首頁
    if (isAuthenticated) {
        return <Navigate to="/" replace />;
    }

    // 渲染內容
    const renderContent = () => {
        if (isLoading) {
            return <LoadingSpinner fullScreen={true} text="登入中..." />;
        }

        if (liffError) {
            return (
                <div className="text-red-500">
                    <p>LIFF 初始化失敗:</p>
                    <p>{liffError}</p>
                </div>
            );
        }

        // LIFF 已載入，且用戶未登入 Line
        if (liff && !liff.isLoggedIn()) {
            return (
                <Button onClick={login} size="lg">
                    使用 Line 登入
                </Button>
            );
        }

        // 預設 (LIFF 正在初始化，或正在處理後端登入)
        return <LoadingSpinner fullScreen={true} text="正在載入 Line 登入..." />;
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="p-8 bg-white rounded-lg shadow-md text-center">
                <h1 className="text-2xl font-bold mb-6">團購系統登入</h1>
                {renderContent()}
            </div>
        </div>
    );
};

export default LoginPage;