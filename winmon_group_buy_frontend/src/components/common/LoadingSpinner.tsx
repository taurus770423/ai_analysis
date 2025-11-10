import React from 'react';

const LoadingSpinner: React.FC = () => {
    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
            <h2>讀取中...</h2>
        </div>
    );
};

export default LoadingSpinner;