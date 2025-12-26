// src/context/AuthContext.tsx
import React, { createContext, useState, useEffect, useContext } from 'react';
import { jwtDecode } from 'jwt-decode';
import api from '../services/api';

interface User {
  sub: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  login: (token: string, refreshToken: string) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType>(null!);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        setUser({ sub: decoded.sub, role: decoded.role || 'USER' });
      } catch (e) {
        logout();
      }
    }
  }, []);

  const login = (token: string, refreshToken: string) => {
    localStorage.setItem('accessToken', token);
    localStorage.setItem('refreshToken', refreshToken);
    const decoded: any = jwtDecode(token);
    setUser({ sub: decoded.sub, role: decoded.role || 'USER' });
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);