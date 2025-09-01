document.addEventListener("DOMContentLoaded", function () {
    const searchButton = document.getElementById("searchButton");

    if (searchButton) {
        searchButton.addEventListener("click", searchBooks);
    }
});

async function searchBooks() {
    const query = document.getElementById("searchInput").value.trim();

    if (!query) {
        alert("검색어를 입력하세요!");
        return;
    }

    try {
        const response = await fetch(`/books/search?query=${query}`); // 📡 API 요청
        // JSON 응답이 맞는지 확인
        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            throw new Error("서버 응답이 올바른 JSON 형식이 아닙니다.");
        }

        const books = await response.json();

        const tableBody = document.getElementById("modalBookTableBody");
        tableBody.innerHTML = ""; // 기존 목록 초기화

        if (books.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" class="text-center">검색 결과가 없습니다.</td></tr>`;
        } else {
            books.forEach(book => {
                const row = document.createElement("tr");

                row.innerHTML = `
                    <td><img src="${book.image}" alt="표지" style="width:50px;"></td>
                    <td>${book.title}</td>
                    <td>${book.author} / ${book.publisher}</td>
                    <td>${book.pubdate}</td>
                    <td>${book.isbn}</td>
                `;

                // 📌 클릭 시 도서 정보 입력되도록 이벤트 추가
                row.addEventListener("click", () => selectBook(book));

                tableBody.appendChild(row);
            });
        }

        // 모달 열기
        const modal = new bootstrap.Modal(document.getElementById("bookModal"));
        modal.show();

    } catch (error) {
        console.error("📚 도서 검색 오류:", error);
        alert("검색 중 오류가 발생했습니다.");
    }
}

// 📌 선택한 도서 정보 자동 입력
function selectBook(book) {
    document.getElementById("title").value = book.title;
    document.getElementById("author").value = book.author;
    document.getElementById("publisher").value = book.publisher;
    document.getElementById("image").value = book.image; // 표지 이미지 URL

    // 모달 닫기
    const modalElement = document.getElementById("bookModal");
    const modalInstance = bootstrap.Modal.getInstance(modalElement);
    modalInstance.hide();
}