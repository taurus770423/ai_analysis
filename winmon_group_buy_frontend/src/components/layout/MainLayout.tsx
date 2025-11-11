// src/components/layout/MainLayout.tsx
import { Outlet, Link } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { Button } from '../ui/Button';

const MainLayout = () => {
    const { logout, lineProfile } = useAuthStore();

    return (
        <div className="min-h-screen flex flex-col">
            {/* 導航欄 */}
            <header className="bg-green-600 text-white shadow-md">
                <nav className="container mx-auto px-4 py-3 flex justify-between items-center">
                    <Link to="/" className="text-xl font-bold">Winmon 團購網</Link>
                    <div className="flex items-center space-x-4">
                        <Link to="/" className="hover:underline">首頁</Link>
                        <Link to="/products" className="hover:underline">所有商品</Link>
                        {lineProfile && (
                            <span className="text-sm">Hi, {lineProfile.displayName}</span>
                        )}
                        <Button onClick={logout} variant="outline" size="sm">
                            登出
                        </Button>
                    </div>
                </nav>
            </header>

            {/* 頁面內容 */}
            <main className="flex-grow container mx-auto p-4">
                <Outlet />
            </main>

            {/* 頁腳 */}
            <footer className="bg-gray-800 text-white p-4 text-center text-sm">
                © 2025 雲蒙 (Cloud Meng)
            </footer>
        </div>
    );
};

export default MainLayout;