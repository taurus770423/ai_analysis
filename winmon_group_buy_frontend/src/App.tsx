import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import AppLayout from './components/common/AppLayout';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import AuthCallbackPage from './pages/AuthCallbackPage';
import LiffEntryPage from './pages/LiffEntryPage';
import ProtectedRoute from './components/auth/ProtectedRoute';

function App() {
    return (
        <AuthProvider>
            <BrowserRouter basename="/group-buy-dev">
                <Routes>
                    {/* 路由 1: 電腦版網站 (有導覽列) */}
                    <Route path="/" element={<AppLayout />}>
                        <Route index element={<HomePage />} />
                        <Route path="login" element={<LoginPage />} />

                        {/* 網頁登入回呼 (這個頁面不需要佈局) */}
                        <Route path="auth/callback" element={<AuthCallbackPage />} />

                        {/* 受保護的路由 */}
                        <Route element={<ProtectedRoute />}>
                            <Route path="profile" element={<ProfilePage />} />
                            {/* 其他需要登入的頁面 (例如：購物車, 訂單) */}
                        </Route>
                    </Route>

                    {/* 路由 2: LIFF 進入點 (通常沒有佈局) */}
                    <Route path="/liff" element={<LiffEntryPage />} />

                    {/* TODO: 404 頁面 */}
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;