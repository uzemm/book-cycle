# 📚 BookCycle - 중고도서 구매 및 대여 플랫폼

### 🗂️ 프로젝트 개요

- **서비스명**: BookCycle
- **기간**: 2025.02.11~
- **목표**: 중고도서를 구매하거나 대여할 수 있는 플랫폼 개발

---

## 🛠 기술 스택

| 분야       | 사용 기술                                  |
|------------|---------------------------------------------|
| Backend    | Spring Boot, Spring Security, JPA, JWT, Redis|


---

## 📌 주요 기능

### 사용자

- 회원가입 / 로그인 (이메일 인증, 소셜 로그인)
- 이메일, 비밀번호 변경
- 장바구니 / 관심도서
- 주문 / 결제 / 취소
- 대여 / 반납 / 예약
- [ ] 알림 (주문 확정, 반납 기한 등)

### 관리자

- 판매/대여 도서 등록 / 수정 / 삭제
- [ ] 회원 목록 / 상세 / 수정 / 삭제
- [ ] 판매/대여 도서 현황 조회
- [ ] 주문 관리 (상태 변경 등)
- [ ] 매출 통계 / 연체 수익 확인

---

## 📈 ERD / 시스템 아키텍처

![erd](https://github.com/user-attachments/assets/2ad3cf85-70ab-40b8-a456-795a8cd31c0a)


- 공통 도서 테이블 + 판매/대여 분리
- 주문 - 주문항목 - 결제 연관 구조
- 예약, 대여이력 분리로 상태 추적 용이

---

## 💡 구현 포인트 & 고민했던 부분

### ✅ 대여 도서 예약 처리
- 주문 취소 시 다음 예약자 결제 대기 전환 처리

### ✅ 주문 단위 반납
- 주문 ID 기반 묶음 처리
- 연체료 계산 / 포인트 정산까지 포함

---

## 🧪 테스트

- JUnit + Mockito 단위 테스트
- API 테스트 (Postman / Swagger)

---

## 📝 트러블슈팅

| 문제 상황 | 해결 방법 |
|-----------|------------|
| 반납 시 도서 상태가 변경되지 않아 응답에 반영 안 됨 | 결제 처리 후 반납 처리 순서로 변경 |
| Lazy 로딩된 컬렉션 직렬화 오류 | DTO로 필요한 정보만 추출 |
| `/payment/success.html?...` 요청이 @GetMapping(`/payment/{paymentKey}`)에 매칭됨 | 컨트롤러 URL 변경하여 패턴 충돌 방지 |
| `@AuthenticationPrincipal`의 userDetails가 null | `JwtTokenProvider`에서 `CustomUserDetails` 반환하도록 수정 |


---

## 🚧 추가 고려/계획 중인 기능
- [ ] 예약 대기 인원 확장 (1명 → 2명)
- [ ] 사용자 리뷰 기능 도입 (도서/거래 후기)
- [ ] 카카오 소셜 로그인 연동
- [ ] 비밀번호 찾기 기능 추가 (이메일 기반)
- [ ] 인증 코드 재전송 기능 (시간 제한 포함)
- [ ] 메일 전송 방식 SMTP → 클라우드 서비스 (예: Amazon SES, Mailgun 등) 전환

---

## 🎯 배운 점

- 실무 수준의 도메인 설계 중요성
- 예외 설계와 예외 메시지의 명확함이 사용자 경험에 큰 영향
- 상태 관리(주문, 대여, 예약 등)의 중요성
- 트랜잭션, 연관관계, 성능을 동시에 고려해야 하는 복잡한 로직에 대한 경험
- 테스트 코드의 중요성

---

## 📢 느낀 점 / 회고

- 완성도 높은 프로젝트를 해보니 서비스 흐름을 끝까지 책임지는 게 얼마나 중요한지 체감했다.
- 데드라인을 지키는게 쉽지 않다.

