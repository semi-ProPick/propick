document.querySelectorAll(".category_menu").forEach((item) => {
  item.addEventListener("click", () => {
    // 다른 모든 항목에서 active 클래스 제거
    document.querySelectorAll(".category_menu").forEach((cate) => {
      cate.classList.remove("active");
    });

    // 클릭한 항목에 active 클래스 추가
    item.classList.add("active");
  });
});

document.querySelectorAll(".product_cate").forEach((item) => {
  item.addEventListener("click", () => {
    // 다른 모든 항목에서 active 클래스 제거
    document.querySelectorAll(".product_cate").forEach((cate) => {
      cate.classList.remove("active");
    });

    // 클릭한 항목에 active 클래스 추가
    item.classList.add("active");
  });
});

document.addEventListener("DOMContentLoaded", function () {
  const listItems = document.querySelectorAll(".category_wrap ul li");

  listItems.forEach(function (item) {
    item.addEventListener("click", function () {
      // 모든 리스트 항목에서 active 클래스 제거
      listItems.forEach(function (li) {
        li.classList.remove("active");
      });

      // 클릭된 항목에 active 클래스 추가
      item.classList.add("active");
    });
  });
});

document.addEventListener("DOMContentLoaded", function () {
  const ageCategory = document.getElementById("age_category"); // '연령별' 카테고리 li
  const ageList = document.querySelector(".age_list"); // 'age_list' ul
  const otherCategories = document.querySelectorAll(
    ".product_cate:not(#age_category)"
  ); // '연령별' 제외한 다른 카테고리들
  const ageCates = document.querySelectorAll(".age_cate");

  // 연령별 클릭 시 age_list 보이기 (숨겨지지 않도록 수정)
  ageCategory.addEventListener("click", function () {
    // 다른 카테고리 클릭 시 age_list 숨기기
    otherCategories.forEach((category) => {
      category.classList.remove("active");
    });

    // 연령별 클릭하면 항상 active 유지
    ageList.classList.add("active");
  });

  // 다른 카테고리 클릭 시, age_list 숨기기
  otherCategories.forEach((category) => {
    category.addEventListener("click", function () {
      ageList.classList.remove("active"); // 연령별이 아닌 카테고리 클릭하면 숨김
    });
  });

  // 연령별 리스트(age_cate) 클릭 시 색상 변경
  ageCates.forEach((cate) => {
    cate.addEventListener("click", function () {
      // 기존 선택된 항목의 active 제거
      ageCates.forEach((item) => item.classList.remove("active"));
      // 현재 클릭한 항목에 active 추가
      cate.classList.add("active");
    });
  });
});

document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.querySelector(".seach_input"); // 검색 입력창
  const productsWrap = document.querySelector(".products_wrap"); // 기존 상품 영역
  const searchResultsWrap = document.querySelector(".search_results_wrap"); // 검색 결과 영역
  const searchResults = document.querySelector(".search_results"); // 검색 결과 리스트
  const pagination = document.querySelector(".pagination"); // 페이지네이션
  let allProducts = []; // 전체 상품 데이터 저장
  let currentPage = 1; // 현재 페이지
  const itemsPerPage = 6; // 한 페이지당 6개 표시

  // 더미 데이터 (서버에서 불러올 데이터 대신)
  allProducts = [
    { name: "마이프로틴", image: "/Propick/images/rank1.png" },
    { name: "마이프로틴", image: "/Propick/images/rank2.png" },
    { name: "마이프로틴", image: "/Propick/images/rank3.png" },
    { name: "유청 프로틴", image: "/Propick/images/rank4.png" },
    { name: "카제인 프로틴", image: "/Propick/images/rank5.png" },
    { name: "식물성 프로틴", image: "/Propick/images/rank6.png" },
  ];

  // 검색 이벤트
  searchInput.addEventListener("input", function () {
    const query = searchInput.value.trim().toLowerCase();

    // 검색어가 없으면 원래 화면으로 복귀
    if (query === "") {
      searchResultsWrap.style.display = "none";
      productsWrap.style.display = "block";
      pagination.style.display = "none"; // 페이지네이션 숨기기
      return;
    }

    const filteredProducts = allProducts.filter((product) =>
      product.name.toLowerCase().includes(query)
    );

    currentPage = 1; // 검색 시 페이지 초기화
    renderProducts(filteredProducts);
  });

  // 🔹 검색 결과 렌더링 함수
  function renderProducts(productArray) {
    searchResults.innerHTML = ""; // 기존 목록 초기화
    pagination.innerHTML = ""; // 페이지네이션 초기화

    if (productArray.length === 0) {
      searchResultsWrap.style.display = "block";
      productsWrap.style.display = "none";
      searchResults.innerHTML = "<p>검색 결과가 없습니다.</p>";
      pagination.style.display = "none"; // 검색 결과 없을 때 페이지네이션 숨기기
      return;
    }

    // 페이지네이션 적용
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const paginatedProducts = productArray.slice(start, end);

    paginatedProducts.forEach((product) => {
      const li = document.createElement("li");
      li.innerHTML = `
            <img src="${product.image}" alt="${product.name}" />
            <p>${product.name}</p>
          `;
      searchResults.appendChild(li);
    });

    // 기존 상품 리스트 숨기고 검색 결과 표시
    productsWrap.style.display = "none";
    searchResultsWrap.style.display = "block";
    pagination.style.display = "block"; // 페이지네이션 표시
    renderPagination(productArray);
  }

  // 🔹 페이지네이션 생성 함수
  function renderPagination(productArray) {
    pagination.innerHTML = "";
    const totalPages = Math.ceil(productArray.length / itemsPerPage);

    // 페이지네이션 항상 표시되도록 수정
    pagination.style.display = "block";

    const prevButton = document.createElement("button");
    prevButton.textContent = "이전";
    prevButton.disabled = currentPage === 1;
    prevButton.addEventListener("click", () => {
      if (currentPage > 1) {
        currentPage--;
        renderProducts(productArray);
      }
    });

    const nextButton = document.createElement("button");
    nextButton.textContent = "다음";
    nextButton.disabled = currentPage === totalPages;
    nextButton.addEventListener("click", () => {
      if (currentPage < totalPages) {
        currentPage++;
        renderProducts(productArray);
      }
    });

    pagination.appendChild(prevButton);
    for (let i = 1; i <= totalPages; i++) {
      const pageButton = document.createElement("button");
      pageButton.textContent = i;
      if (i === currentPage) pageButton.classList.add("active");
      pageButton.addEventListener("click", () => {
        currentPage = i;
        renderProducts(productArray);
      });
      pagination.appendChild(pageButton);
    }
    pagination.appendChild(nextButton);
  }
});
