import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { Bell, ShoppingCart, Search } from "lucide-react";

export default function MainHeader() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [name, setName] = useState<string>("");
  const navigate = useNavigate();

  // 로그인 사용자 정보 가져오기
  useEffect(() => {
    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) {
      setIsLoggedIn(false);
      return;
    }

    const fetchName = async () => {
      try {
        const response = await axios.get<{ name: string }>("/members/me", {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        setName(response.data.name);
        setIsLoggedIn(true);
      } catch (error) {
        console.error("인증 실패", error);
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        setIsLoggedIn(false);
        navigate("/login");
      }
    };

    fetchName();
  }, []);

  const handleLogout = async () => {
    try {
      const accessToken = localStorage.getItem("accessToken");

      await axios.post("/auth/logout", null, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });

      // 클라이언트 상태 정리
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      setIsLoggedIn(false);
      setName("");
    } catch (error) {
      console.error("로그아웃 실패:", error);
    }
  };

  return (
    <header className="border-b bg-white">
      {/* 상단 로고 & 검색 & 아이콘 */}
      <div className="flex items-center justify-between px-6 py-3">
        {/* 로고 */}
        <div className="flex items-center gap-2">
          <img src="/logo.png" alt="BookCycle" className="h-8" />
          <span className="text-lg font-bold">BookCycle</span>
        </div>

        {/* 검색창 */}
        <div className="flex-1 max-w-xl mx-8">
          <div className="flex border rounded-lg overflow-hidden">
            <input
              type="text"
              placeholder="제목, 작가, 태그, 시리즈명, Pick을 검색해 보세요."
              className="w-full px-4 py-2 outline-none"
            />
            <button className="bg-blue-500 hover:bg-blue-600 text-white px-4 flex items-center justify-center">
              <Search size={20} />
            </button>
          </div>
        </div>

        {/* 오른쪽 메뉴 */}
        <div className="flex items-center gap-4">
          {isLoggedIn ? (
            <>
              <span className="text-sm font-medium">{name}님</span>
              <button
                onClick={handleLogout}
                className="text-sm px-3 py-1 border rounded hover:bg-gray-100"
              >
                로그아웃
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => navigate("/login")}
                className="text-sm px-3 py-1 border rounded hover:bg-gray-100"
              >
                로그인
              </button>
              <button
                onClick={() => navigate("/signup")}
                className="text-sm px-3 py-1 border rounded hover:bg-gray-100"
              >
                회원가입
              </button>
            </>
          )}

          <Bell className="cursor-pointer" />
          <ShoppingCart className="cursor-pointer" />
        </div>
      </div>

      {/* 메뉴바 */}
      <nav className="flex gap-6 px-6 py-2 border-t text-sm font-medium">
        <a href="#">📚 베스트 도서</a>
        <a href="#">🆕 신간 도서</a>
        <a href="#">🔍 맞춤검색</a>
        <a href="#">🔥 인기 도서관</a>
        <a href="#">🏪 우도샵</a>
        <a href="#">📷 중고샵</a>
        <a href="#">📖 PICK</a>
        <a href="#">🎯 리딩게이트</a>
        <a href="#">📌 관심도서</a>
        <a href="#">📋 주문현황</a>
        <a href="#">🏠 내 도서관</a>
      </nav>
    </header>
  );
}
