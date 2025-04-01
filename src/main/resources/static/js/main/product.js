// 기존 코드 유지
document.querySelectorAll(".category_menu").forEach((item) => {
  item.addEventListener("click", () => {
    document.querySelectorAll(".category_menu").forEach((cate) => {
      cate.classList.remove("active");
    });
    item.classList.add("active");
  });
});

document.querySelectorAll(".product_cate").forEach((item) => {
  item.addEventListener("click", () => {
    document.querySelectorAll(".product_cate").forEach((cate) => {
      cate.classList.remove("active");
    });
    item.classList.add("active");
  });
});

// 나머지 기존 코드 유지 (category_wrap, age_category 등)

// 검색 및 페이지네이션 코드 수정
document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.querySelector(".search_input"); // 오타 수정
  const productsWrap = document.querySelector(".products_wrap");
  const searchResultsWrap = document.querySelector(".search_results_wrap");
  const searchResults = document.querySelector(".search_results");
  const pagination = document.querySelector(".pagination");
  let allProducts = [];
  let currentPage = 1;
  const itemsPerPage = 6;

  if (!searchInput) {
    console.error('Search input with class "search_input" not found');
    return;
  }

  // 서버에서 데이터 가져오기 (더미 데이터 대신)
  fetch(`${contextPath}/api/products`) // contextPath는 HTML에서 Thymeleaf로 주입됨
      .then(response => response.json())
      .then(data => {
        allProducts = data.products || [];
      })
      .catch(error => console.error('Error fetching products:', error));

  searchInput.addEventListener("input", function () {
    const query = searchInput.value.trim().toLowerCase();
    if (query === "") {
      searchResultsWrap.style.display = "none";
      productsWrap.style.display = "block";
      pagination.style.display = "none";
      return;
    }
    const filteredProducts = allProducts.filter((product) =>
        product.name.toLowerCase().includes(query)
    );
    currentPage = 1;
    renderProducts(filteredProducts);
  });

  // 북마크 토글 함수 추가
  const favoriteIcons = document.querySelectorAll(".favorite-icon");
  favoriteIcons.forEach(icon => {
    icon.addEventListener("click", function () {
      toggleFavorite(this);
    });
  });

  function toggleFavorite(element) {
    const productId = element.getAttribute("data-product-id");
    const isBookmarked = element.classList.contains("favorited");
    const url = `${contextPath}/api/bookmarks/toggle`;

    fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        productId: productId,
        isBookmarked: !isBookmarked,
      }),
    })
        .then(response => {
          if (response.status === 401) {
            alert("로그인이 필요합니다.");
            window.location.href = `${contextPath}/login`;
            throw new Error("Unauthorized");
          }
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          return response.json();
        })
        .then(data => {
          if (data.success) {
            element.classList.toggle("favorited");
            alert(isBookmarked ? "북마크가 해제되었습니다." : "북마크가 등록되었습니다.");
          } else {
            alert("북마크 업데이트에 실패했습니다: " + (data.message || "알 수 없는 오류"));
          }
        })
        .catch(error => {
          console.error("Error:", error);
          alert("북마크 업데이트 중 오류가 발생했습니다: " + error.message);
        });
  }

  function renderProducts(productArray) {
    searchResults.innerHTML = "";
    pagination.innerHTML = "";
    if (productArray.length === 0) {
      searchResultsWrap.style.display = "block";
      productsWrap.style.display = "none";
      searchResults.innerHTML = "<p>검색 결과가 없습니다.</p>";
      pagination.style.display = "none";
      return;
    }
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const paginatedProducts = productArray.slice(start, end);

    paginatedProducts.forEach((product) => {
      const li = document.createElement("li");
      li.innerHTML = `
        <img src="${product.image || `${contextPath}/images/default.png`}" alt="${product.name}" />
        <p>${product.name}</p>
      `;
      searchResults.appendChild(li);
    });

    productsWrap.style.display = "none";
    searchResultsWrap.style.display = "block";
    pagination.style.display = "block";
    renderPagination(productArray);
  }

  function renderPagination(productArray) {
    // 기존 코드 유지
  }
});

// contextPath는 HTML에서 Thymeleaf로 주입: const contextPath = /*[[${contextPath}]]*/ '';