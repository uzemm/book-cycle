// src/pages/EmailVerificationPage.tsx
declare global {
  interface Window {
    daum: any;
  }
}

import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "@/components/ui/card";
import { Label } from "@/components/ui/label";

export default function EmailVerificationPage() {
  const [email, setEmail] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const [errors, setErrors] = useState<{ [key: string]: string}>({});
  const [cooldown, setCooldown] = useState(0);
  
    useEffect(() => {
    if (location.state?.email) {
        setEmail(location.state.email);
    }
    }, [location.state]);

    useEffect(() => {
    if (cooldown > 0) {
      const timer = setTimeout(() => setCooldown(cooldown - 1), 1000);
      return () => clearTimeout(timer);
    }
    }, [cooldown]);

  const handleVerify = async () => {
    try {
      await axios.post("/auth/verify-check", { email, verificationCode });
      alert("이메일 인증이 완료되었습니다!");
      navigate("/login");
    } catch (error: any) {
      console.log("❗ 에러 확인:", error.response?.data);

        const data = error.response?.data;

        if (data?.errorMessage) {
          setErrors({ global: data.errorMessage }); // 글로벌 에러 처리
        } else if (data && typeof data === "object") {
          setErrors(data); // 필드 에러용
        } else {
          setErrors({ global: "예상치 못한 오류입니다." });
        }
      }
  };

  const handleResend = async () => {
  try {
    await axios.post("/auth/email/resend?", { email });
    alert("인증 메일을 재전송했습니다.");
    setCooldown(60); // 60초 쿨타임
  } catch (error: any) {
        console.log("❗ 에러 확인:", error.response?.data);

        const data = error.response?.data;

        if (data?.errorMessage) {
          setErrors({ global: data.errorMessage }); // 글로벌 에러 처리
        } else if (data && typeof data === "object") {
          setErrors(data); // 필드 에러용
        } else {
          setErrors({ global: "예상치 못한 오류입니다." });
        }
      }
};

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <Card className="w-full max-w-md p-6 shadow-md">
        <CardHeader>
          <CardTitle className="text-2xl font-bold text-center">이메일 인증</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">이메일</Label>
            <div className="relative">
              <Input
                id="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled
                className="pr-20"
              />
              <Button
                variant="outline"
                size="sm"
                onClick={handleResend}
                disabled={cooldown > 0}
                className="absolute right-1 top-1/2 -translate-y-1/2 h-8"
              >
                {cooldown > 0 ? `${cooldown}초` : "재전송"}
              </Button>
            </div>
            {errors.email && <p className="text-sm text-red-500">{errors.email}</p>}
          </div>
          <div className="space-y-2">
            <Label htmlFor="verificationCode">인증코드</Label>
            <Input
              id="verificationCode"
              type="text"
              placeholder="인증코드를 입력하세요"
              value={verificationCode}
              onChange={(e) => setVerificationCode(e.target.value)}
            />
            {errors.verificationCode && <p className="text-sm text-red-500">{errors.verificationCode}</p>}
          </div>
        </CardContent>
        <CardFooter>
          <Button className="w-full" onClick={handleVerify}>
            인증하기
          </Button>
        </CardFooter>
        {errors.global && (
          <p className="text-red-500 text-sm mt-4 text-center">
            {errors.global}
          </p>
        )}
      </Card>
    </div>
  );
}
