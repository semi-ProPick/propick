document.getElementById("productForm").addEventListener("submit", function (event) {
    let isValid = true;

    // 상품 ID 유효성 검사
    const productId = document.getElementById("productId");
    const productIdError = document.getElementById("productIdError");
    if (productId.value.trim() === "") {
        productId.classList.add("error");
        productIdError.style.display = "block";
        isValid = false;
    } else {
        productId.classList.remove("error");
        productIdError.style.display = "none";
    }

    // 상품 이름 유효성 검사
    const productName = document.getElementById("productName");
    const productNameError = document.getElementById("productNameError");
    if (productName.value.trim() === "") {
        productName.classList.add("error");
        productNameError.style.display = "block";
        isValid = false;
    } else {
        productName.classList.remove("error");
        productNameError.style.display = "none";
    }

    // 유효성 검사 실패 시 제출 방지
    if (!isValid) {
        event.preventDefault();
    } else {
        alert("등록되었습니다.");
    }
});

// 등록 버튼 클릭 시 폼 제출 트리거
document.querySelector(".btn-register").addEventListener("click", function () {
    document.getElementById("productForm").dispatchEvent(new Event("submit", { cancelable: true }));
});

// 취소 버튼 클릭 시 폼 초기화
document.querySelector(".btn-cancel").addEventListener("click", function () {
    document.getElementById("productForm").reset();
});