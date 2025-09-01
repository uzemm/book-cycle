document.addEventListener("DOMContentLoaded", function () {
    const emailInput = document.getElementById("email");

    // localStorage에서 이메일 불러오기
    const savedEmail = localStorage.getItem("email");
    if (savedEmail) {
        console.log("🔹 자동 입력된 이메일:", savedEmail);
        emailInput.value = savedEmail;
        emailInput.readOnly = true; // 수정 불가
    } else {
        console.warn("⚠️ 이메일이 localStorage에 저장되지 않음.");
        emailInput.readOnly = false; // 직접 입력 가능
    }

    document.getElementById("verifyForm").addEventListener("submit", async function (event) {
        event.preventDefault(); // 기본 폼 제출 방지

        const formData = {
            email: emailInput.value,
            verificationCode: document.getElementById("verificationCode").value
        };

        // 🔥 필드별 오류 메시지 초기화
        document.querySelectorAll(".field-error").forEach(el => {
            el.textContent = "";
            el.style.display = "none";
        });

        try {
            const response = await fetch("/auth/verify-check", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData)
            });

            console.log("✅ 응답 상태 코드:", response.status);

            if (!response.ok) {
                const errorData = await response.json();
                console.error("❌ 인증 실패:", errorData);

                // 🚀 필드별 오류 메시지 표시
                Object.entries(errorData).forEach(([field, message]) => {
                    let fieldElement = document.getElementById(`${field}-error`);
                    if (fieldElement) {
                        console.log(`🔍 ${field} 오류 메시지 표시: ${message}`);
                        fieldElement.textContent = message;
                        fieldElement.style.display = "block";
                    }
                });

                return;
            }

            console.log("🎉 인증 성공!");

            // ✅ 인증 성공 시 이메일 정보 삭제
            localStorage.removeItem("email");

            // 2초 후 로그인 페이지로 이동
            setTimeout(() => {
                window.location.href = "login-form";
            }, 2000);

        } catch (error) {
            console.error("🔥 인증 오류:", error);
        }
    });
});

