document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.querySelector(".search_input");
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

  // 제품 데이터 가져오기 (contextPath는 HTML에서 제공)
  fetch(`${contextPath}/api/product`)
      .then(response => {
        if (!response.ok) throw new Error(`Failed to fetch products: ${response.status}`);
        return response.json();
      })
      .then(data => {
        allProducts = data || [];
        console.log("Fetched products:", allProducts);
        renderProducts(allProducts);
      })
      .catch(error => console.error('Error fetching products:', error));

  // 검색 기능
  searchInput.addEventListener("input", function () {
    const query = searchInput.value.trim().toLowerCase();
    if (query === "") {
      searchResultsWrap.style.display = "none";
      productsWrap.style.display = "block";
      pagination.style.display = "none";
      renderProducts(allProducts);
      return;
    }
    const filteredProducts = allProducts.filter((product) =>
        product.productName.toLowerCase().includes(query)
    );
    currentPage = 1;
    renderProducts(filteredProducts);
  });

  // 북마크 토글 함수
  function toggleFavorite(element) {
    const productId = element.getAttribute("data-product-id");
    if (!productId) {
      console.error("Product ID not found on element:", element);
      alert("제품 ID를 찾을 수 없습니다.");
      return;
    }

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
            return Promise.reject(new Error("Unauthorized"));
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
            alert("북마크 처리에 실패했습니다: " + (data.message || "알 수 없는 오류"));
          }
        })
        .catch(error => {
          console.error("Error:", error);
          alert("북마크 처리 중 오류가 발생했습니다: " + error.message);
        });
  }

  // 제품 렌더링 함수
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
      li.className = "product-item";
      const imageUrl = product.productImages && product.productImages.length > 0
          ? `${contextPath}${product.productImages[0]}`
          : `${contextPath}/images/product-img/default.png`;
      li.innerHTML = `
        <a href="${contextPath}/products/${product.productId}">
          <div class="product-images">
            <img src="${imageUrl}" alt="${product.productName}" onerror="this.src='${contextPath}/images/product-img/default.png'" />
          </div>
          <p>${product.productName || '상품명'}</p>
        </a>
        <div class="favorite-icon ${product.isBookmarked ? 'favorited' : ''}" data-product-id="${product.productId}">
          <i class="fas fa-star"></i>
        </div>
      `;
      searchResults.appendChild(li);

      li.querySelector(".favorite-icon").addEventListener("click", function (e) {
        e.preventDefault(); // 링크 이동 방지
        toggleFavorite(this);
      });
    });

    productsWrap.style.display = "none";
    searchResultsWrap.style.display = "block";
    pagination.style.display = "block";
    renderPagination(productArray);
  }
});