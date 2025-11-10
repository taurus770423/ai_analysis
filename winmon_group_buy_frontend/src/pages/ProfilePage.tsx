import React from 'react';
import { useAuth } from '../hooks/useAuth';
import { Navigate } from 'react-router-dom';

const ProfilePage: React.FC = () => {
    const { user, isLoading } = useAuth();

    if (isLoading) {
        return <div>正在載入使用者資料...</div>;
    }

    if (!user) {
        // 雖然有 ProtectedRoute，但多一層防護
        return <Navigate to="/login" replace />;
    }

    return (
        <div>
            <h2>個人資料 (受保護)</h2>
            {user.pictureUrl && (
                <img
                    src={user.pictureUrl}
                    alt="Profile"
                    style={{ width: 100, height: 100, borderRadius: '50%' }}
                />
            )}
            <p><strong>你好, {user.displayName}</strong></p>
            <p>Line User ID: {user.lineUserId}</p>
            <p>Email: {user.email || 'N/A'}</p>
            <p>Role: {user.role}</p>
        </div>
    );
};

export default ProfilePage;