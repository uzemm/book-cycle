// src/pages/LoginPage.tsx
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "@/components/ui/card";
import { Label } from "@/components/ui/label";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const response = await axios.post("/auth/login", { email, password });
       const { accessToken, refreshToken } = response.data;

      // 로컬스토리지 저장
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);

      // axios 기본 헤더에 등록 (다음 요청부터 자동으로 포함됨)
      axios.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;

      navigate("/main");
    } catch (error: any) {

      const data = error.response?.data;

      if (data?.errorCode === "EMAIL_NOT_VERIFIED") {
      // 이메일 인증 페이지로 이동하면서 이메일 값 전달
      navigate("/verify", { state: { email } });
      return;
    }

      if (data?.errorMessage) {
        setErrors({ global: data.errorMessage });
      } else if (data && typeof data === "object") {
        setErrors(data);
      } else {
        setErrors({ global: "예상치 못한 오류입니다." });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <Card className="w-full max-w-md p-6 shadow-md">
        <CardHeader>
          <CardTitle className="text-2xl font-bold text-center">로그인</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">이메일</Label>
            <Input
              id="email"
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            {errors.email && <p className="text-sm text-red-500">{errors.email}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="password">비밀번호</Label>
            <Input
              id="password"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            {errors.password && <p className="text-sm text-red-500">{errors.password}</p>}
          </div>

          <div className="flex justify-between text-sm">
            <Link to="/forgot-password" className="text-blue-600 hover:underline">
              비밀번호 찾기
            </Link>
            <Link to="/signup" className="text-blue-600 hover:underline">
              회원가입
            </Link>
          </div>
        </CardContent>
        <CardFooter className="flex flex-col gap-3">
          <Button className="w-full" onClick={handleLogin} disabled={loading}>
            {loading ? "로그인 중..." : "로그인"}
          </Button>
          {errors.global && (
            <p className="text-red-500 text-sm text-center">
              {errors.global}
            </p>
          )}
        </CardFooter>
      </Card>
    </div>
  );
}
