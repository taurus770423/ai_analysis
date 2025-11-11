// src/pages/HomePage.tsx
import { useAuthStore } from '../store/authStore';

const HomePage = () => {
    const lineProfile = useAuthStore((state) => state.lineProfile);

    return (
        <div>
            <h1 className="text-3xl font-bold mb-4">歡迎, {lineProfile?.displayName}!</h1>

            {lineProfile?.pictureUrl && (
                <img
                    src={lineProfile.pictureUrl}
                    alt="Profile"
                    className="w-24 h-24 rounded-full mb-4"
                />
            )}

            <p>這裡是團購系統首頁。</p>

            {/* 在此處開始您的團購列表 */}
        </div>
    );
};

export default HomePage;