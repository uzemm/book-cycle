document.addEventListener("DOMContentLoaded", function () {
    const signupForm = document.getElementById("signupForm");

    if (signupForm) {
        signupForm.addEventListener("submit", async function (event) {
            event.preventDefault(); // 기본 폼 제출 방지

            const formData = {
                email: document.getElementById("email").value,
                password: document.getElementById("password").value,
                name: document.getElementById("name").value,
                phone: document.getElementById("phone").value,
                address: document.getElementById("address").value
            };

            // 🔥 필드별 오류 메시지 초기화
            document.querySelectorAll(".field-error").forEach(el => {
                el.textContent = "";
                el.style.display = "none";
            });

            try {
                const response = await fetch("/auth/signup", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(formData)
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    console.error("❌ 회원가입 실패:", errorData);

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

                console.log("🎉 회원가입 성공!");
                // ✅ 이메일 저장 후 인증 페이지로 이동
                localStorage.setItem("email", formData.email); // 🚀 이메일 저장
                window.location.href = "verify-form"; // 🚀 인증 페이지로 이동

            } catch (error) {
                console.error("🔥 회원가입 오류:", error);
            }
        });
    }
});
