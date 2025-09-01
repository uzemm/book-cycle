import React, { createContext, useContext, useState, useEffect } from "react";
import axiosInstance from "./axiosInstance";

type User = {
  name: string;
  email: string;
};

type AuthContextType = {
  user: User | null;
  isAuthenticated: boolean;
  login: (accessToken: string, refreshToken: string) => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);

  const isAuthenticated = !!user;

  // 앱 시작 시 localStorage 토큰 확인하고 me API 호출
  useEffect(() => {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken) {
      axiosInstance
        .get("/members/me")
        .then((res) => setUser(res.data))
        .catch(() => {
          // 토큰이 유효하지 않으면 로그아웃 처리
          logout();
        });
    }
  }, []);

  const login = async (accessToken: string, refreshToken: string) => {
    // 토큰 저장
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);

    // me API 호출해서 사용자 정보 가져오기
    const res = await axiosInstance.get("/members/me");
    setUser(res.data);
  };

  const logout = () => {
    localStorage.clear();
    setUser(null);
    window.location.href = "/login"; // 라우팅 처리
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
};
