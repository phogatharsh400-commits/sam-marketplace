import React, { createContext, useContext, useState, useEffect } from 'react';
import { AuthResponse, UserDto, RoleType } from '../types';
import { userApi, authApi } from '../api';

interface AuthContextType {
  user: UserDto | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentialsOrAuthData: any, password?: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshProfile: () => Promise<void>;
  hasRole: (role: RoleType) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserDto | null>(() => {
    const saved = localStorage.getItem('sam_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState<string | null>(localStorage.getItem('sam_access_token'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const fetchUserProfile = async () => {
    try {
      const res = await userApi.getMe();
      if (res.success && res.data) {
        const u = {
          ...res.data,
          name: res.data.fullName,
          accountStatus: res.data.status,
        };
        setUser(u);
        localStorage.setItem('sam_user', JSON.stringify(u));
      }
    } catch {
      setUser(null);
      localStorage.removeItem('sam_access_token');
      localStorage.removeItem('sam_refresh_token');
      localStorage.removeItem('sam_user');
      setToken(null);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      fetchUserProfile();
    } else {
      setIsLoading(false);
    }
  }, [token]);

  const login = async (credentialsOrAuthData: any, password?: string) => {
    if (typeof credentialsOrAuthData === 'string' && password) {
      const res = await authApi.login({ mobileNumber: credentialsOrAuthData, password });
      const authData = res.data;
      localStorage.setItem('sam_access_token', authData.accessToken);
      localStorage.setItem('sam_refresh_token', authData.refreshToken);
      setToken(authData.accessToken);
      await fetchUserProfile();
    } else if (credentialsOrAuthData?.accessToken) {
      const authData = credentialsOrAuthData as AuthResponse;
      localStorage.setItem('sam_access_token', authData.accessToken);
      localStorage.setItem('sam_refresh_token', authData.refreshToken);
      setToken(authData.accessToken);
      await fetchUserProfile();
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch {
      // ignore
    } finally {
      localStorage.removeItem('sam_access_token');
      localStorage.removeItem('sam_refresh_token');
      localStorage.removeItem('sam_user');
      setUser(null);
      setToken(null);
    }
  };

  const refreshProfile = async () => {
    await fetchUserProfile();
  };

  const hasRole = (role: RoleType): boolean => {
    return user?.role === role;
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token && !!user,
        isLoading,
        login,
        logout,
        refreshProfile,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
