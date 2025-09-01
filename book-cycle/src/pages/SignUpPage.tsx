// src/pages/SignUpPage.tsx
declare global {
  interface Window {
    daum: any;
  }
}

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "@/components/ui/card";
import { Label } from "@/components/ui/label";

export default function RegisterPage() {
  const [email, setEmail] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [phone, setPhone] = useState("");
  const [zonecode, setZonecode] = useState("");
  const [address, setAddress] = useState("");
  const [detailAddress, setDetailAddress] = useState(""); // 사용자가 입력
  const fullAddress = `${zonecode} ${address} ${detailAddress}`.trim();

  const [errors, setErrors] = useState<{ [key: string]: string}>({});

  const navigate = useNavigate();

  const handleAddressSearch = () => {
  new window.daum.Postcode({
    oncomplete: function (data: any) {
      setZonecode(data.zonecode); // 우편번호
      setAddress(data.address);   // 상세주소 (도로명 기준)
      setDetailAddress(""); // 새로 검색하면 상세주소는 초기화
    },
    }).open();
  };

  const handleSignUp = async () => {
    try {
      await axios.post("auth/signup", {
        email,
        password,
        confirmPassword,
        name,
        phone,
        address : fullAddress,
      });

      alert("회원가입 성공! 이메일 인증을 진행해주세요.");

      // // 성공 시 인증 페이지로 이동
    navigate("/verify", { state: { email } });
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
          <CardTitle className="text-2xl font-bold text-center">회원가입</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">이메일</Label>
            <Input
              id="email"
              type="email"
              placeholder="example@domain.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            {errors.email && <p className="text-sm text-red-500">{errors.email}</p>}
          </div>
          <div className="space-y-2">
            <Label htmlFor="name">이름</Label>
            <Input
              id="name"
              type="text"
              placeholder="이름"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
            {errors.name && <p className="text-sm text-red-500">{errors.name}</p>}
          </div>
          <div className="space-y-2">
            <Label htmlFor="password">비밀번호</Label>
            <Input
              id="password"
              type="password"
              placeholder="비밀번호는 8자 이상 20자 이하로 입력하세요."
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            {errors.password && <p className="text-sm text-red-500">{errors.password}</p>}
          </div>
          <div className="space-y-2">
            <Label htmlFor="confirmPassword">비밀번호 확인</Label>
            <Input
              id="confirmPassword"
              type="password"
              placeholder="비밀번호를 입력하세요."
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
            {errors.confirmPassword && <p className="text-sm text-red-500">{errors.confirmPassword}</p>}
          </div>
          <div className="space-y-2">
            <Label htmlFor="email">전화번호 ('-'제외)</Label>
            <Input
              id="phone"
              type="tel"
              placeholder="01000000000"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
            />
            {errors.phone && <p className="text-sm text-red-500">{errors.phone}</p>}
          </div>
          <div className="space-y-2">
          <Label>주소</Label>
          {errors.address && <p className="text-sm text-red-500">{errors.address}</p>}
          <div className="flex gap-2">
            <Input
              type="text"
              placeholder="우편번호"
              value={zonecode}
              readOnly
              className="w-1/2"
            />
            <Button
              type="button"
              onClick={handleAddressSearch}
              className="w-1/2"
            >
              주소 검색
            </Button>
          </div>

          <Input
            type="text"
            placeholder="주소"
            value={address}
            readOnly
          />
          

          <Input
            type="text"
            placeholder="상세 주소"
            value={detailAddress}
            onChange={(e) => setDetailAddress(e.target.value)}
          />
          </div>
        <Button type="button" onClick={handleAddressSearch}>
          주소 검색
        </Button>
        </CardContent>
        <CardFooter>
          <Button className="w-full" onClick={handleSignUp}>가입하기</Button>
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
