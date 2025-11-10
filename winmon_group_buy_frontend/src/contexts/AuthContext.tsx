import React, { createContext, useState, useEffect } from 'react';
// [修正 1] 將 ReactNode (型別) 分開 import
import type { ReactNode } from 'react';
// [修正 2] 將 User (型別) 改為 type-only import
import type { User } from '../types/User';
import { apiClient, getJwtToken, removeJwtToken, setJwtToken } from '../services/api';
import { environment } from '../utils/environment';

interface AuthContextType {
    user: User | null;
    token: string | null;
    isLoading: boolean;
    webLoginUrl: string;
    loginWithToken: (jwt: string) => Promise<void>;
    logout: () => void;
    initializeAuth: () => Promise<void>;
}

// [修正 3] 告訴 ESLint，我們知道這個 Context 不是元件，請忽略
// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(getJwtToken());
    const [isLoading, setIsLoading] = useState(true);

    const webLoginUrl = environment.webLoginUrl;

    const initializeAuth = async () => {
        const localToken = getJwtToken();
        if (localToken) {
            try {
                await fetchUser(localToken);
            } catch (error) {
                console.error('Failed to initialize auth, token might be expired', error);
                await logout();
            }
        }
        setIsLoading(false);
    };

    const fetchUser = async (jwt: string) => {
        setToken(jwt);
        setJwtToken(jwt);

        // 呼叫後端 /user/me
        const response = await apiClient.get<User>('/user/me');
        setUser(response.data);
    };

    const loginWithToken = async (jwt: string) => {
        setIsLoading(true);
        try {
            await fetchUser(jwt);
        } catch (error) {
            console.error('Login failed', error);
            await logout();
        } finally {
            setIsLoading(false);
        }
    };

    const logout = async () => {
        setUser(null);
        setToken(null);
        removeJwtToken();
    };

    useEffect(() => {
        initializeAuth();
    }, []);

    const value = {
        user,
        token,
        isLoading,
        webLoginUrl,
        loginWithToken,
        logout,
        initializeAuth,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};