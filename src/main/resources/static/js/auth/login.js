document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("login-form");
    if (loginForm) {
        loginForm.addEventListener("submit", submitLogin);
    }
});

async function submitLogin(event) {
    event.preventDefault(); // ✅ 기본 폼 제출 방지 (GET 요청 방지)

    console.log("🚀 로그인 시도 중...");

    const formData = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

    // 🔥 필드별 오류 메시지 초기화
    document.querySelectorAll(".field-error").forEach(el => {
        el.textContent = "";
        el.style.display = "none";
    });

    try {
        const response = await fetch("/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(formData)
        });

        const data = await response.json();

        if (!response.ok) {
            console.error("❌ 로그인 실패:", data);

            // 필드별 오류 메시지 표시
            Object.entries(data).forEach(([field, message]) => {
                let fieldElement = document.getElementById(`${field}-error`);
                if (fieldElement) {
                    console.log(`🔍 ${field} 오류 메시지 표시: ${message}`);
                    fieldElement.textContent = message;
                    fieldElement.style.display = "block";
                }
            });
            return;
        }

        console.log("🎉 로그인 성공!", data);

        alert("로그인 성공!");
        window.location.href = "/admin/sales-form"; // 페이지 이동

    } catch (error) {
        console.error("🚨 로그인 요청 중 오류 발생:", error);
        alert("로그인 중 문제가 발생했습니다. 다시 시도해주세요.");
    }
}


