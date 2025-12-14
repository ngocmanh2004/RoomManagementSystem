export interface RegisterRequest {
  username?: string;
  fullName?: string;
  email: string;
  phone?: string;
  password: string;
  confirmPassword?: string;
  address: string;
  cccd?: string;
  dateOfBirth?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: UserInfo;
  accessToken: string; 
  refreshToken: string; 
}

export interface UserInfo {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phone: string;
  role: number;
  roleName: string;
}

export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phone?: string;
  role: number;
  status: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}