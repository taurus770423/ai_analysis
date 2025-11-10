import React from 'react';
import { useAuth } from '../hooks/useAuth';
import { Link } from 'react-router-dom';

const HomePage: React.FC = () => {
    const { user, webLoginUrl, isLoading } = useAuth();

    if (isLoading) {
        return <div>讀取中...</div>;
    }

    return (
        <div>
            <h2>歡迎來到 Winmon 團購網</h2>
            <p>這裏是公開的首頁，任何人都可以瀏覽。</p>

            {user ? (
                <div>
                    <p>您好, <strong>{user.displayName}</strong>！您已登入。</p>
                    <Link to="/profile" style={{ marginRight: '1rem' }}>前往個人資料頁</Link>
                    <Link to="/liff">切換至 LIFF 視圖</Link>
                </div>
            ) : (
                <div>
                    <p>您尚未登入，登入後即可開始團購。</p>
                    <a
                        href={webLoginUrl}
                        style={{ padding: '8px 15px', background: '#00C300', color: 'white', textDecoration: 'none', borderRadius: '5px' }}
                    >
                        使用 Line 登入 (Web)
                    </a>
                </div>
            )}

            <div style={{ marginTop: '2rem', borderTop: '1px solid #ccc', paddingTop: '1rem' }}>
                <h3>公開商品區</h3>
                <p>
                    (這裡未來會放從後端
                    <code>/api/products/public</code>
                    取得的商品列表)
                </p>
            </div>
        </div>
    );
};

export default HomePage;