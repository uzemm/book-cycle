// src/pages/AdminNotificationPage.tsx

import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
  CardFooter
} from "@/components/ui/card"
import { Badge } from "@/components/ui/badge";

export default function AdminNotificationPage() {
  const notifications = [
    {
      title: "예약 1순위 알림",
      message: "결제 가능한 도서가 있습니다.",
      type: "RESERVATION_FIRST",
      date: "2025-08-03",
      color: "blue",
    },
    {
      title: "연체 알림",
      message: "반납이 지연되었습니다.",
      type: "RENTAL_OVERDUE",
      date: "2025-08-01",
      color: "red",
    },
  ];

  return (
    <div className="p-10 max-w-5xl mx-auto space-y-8">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          📢 전체 알림 관리
        </h1>
        <Button>+ 새 알림 등록</Button>
      </div>

       <Card className="w-full max-w-md mx-auto my-4 shadow-md border rounded-lg">
  <CardHeader>
    <CardTitle className="text-lg font-semibold">알림 제목</CardTitle>
    <CardDescription>설명</CardDescription>
  </CardHeader>
  <CardContent>
    <p className="text-sm text-gray-700">내용이 들어갑니다</p>
  </CardContent>
  <CardFooter>
    <p className="text-xs text-gray-400">보낸 날짜: 2025-08-01</p>
  </CardFooter>
</Card>


    </div>
  );
}
