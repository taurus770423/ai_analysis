import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

const AppLayout: React.FC = () => {
    const { user, logout, webLoginUrl } = useAuth();

    return (
        <div>
            <nav style={{ padding: '1rem', background: '#eee' }}>
                <Link to="/" style={{ marginRight: '1rem' }}>首頁</Link>

                {user ? (
                    <>
                        <Link to="/profile" style={{ marginRight: '1rem' }}>{user.displayName}</Link>
                        <button onClick={logout}>登出</button>
                    </>
                ) : (
                    <a href={webLoginUrl}>使用 Line 登入 (Web)</a>
                )}
            </nav>
            <main style={{ padding: '1rem' }}>
                {/* 子路由的內容會顯示在這裡 */}
                <Outlet />
            </main>
        </div>
    );
};

export default AppLayout;