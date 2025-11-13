// src/contexts/AuthContext.tsx
import { createContext } from 'react';

// 1. 定義 Context 儲存的資料類型
export interface AuthContextType {
    token: string | null;
    isLoading: boolean;
    login: (token: string) => void;
    logout: () => void;
}

// 2. 建立並導出 Context
// 預設值為 undefined，我們將在 useAuth hook 中檢查
export const AuthContext = createContext<AuthContextType | undefined>(undefined);