export interface User {
    id: number;
    lineUserId: string;
    displayName: string;
    pictureUrl: string | null;
    email: string | null;
    role: string;
    createTime: string; // (Instant 會被序列化為 string)
    lastLoginTime: string | null;
}