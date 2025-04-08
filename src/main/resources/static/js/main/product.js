document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.querySelector(".search_input");
  const productsWrap = document.querySelector(".products_wrap");
  const searchResultsWrap = document.querySelector(".search_results_wrap");
  const searchResults = document.querySelector(".search_results");
  const pagination = document.querySelector(".pagination");
  const categoryMenus = document.querySelectorAll(".category_menu");
  const categoryWraps = document.querySelectorAll(".category_wrap ul li");
  const discountButtons = document.querySelectorAll("a[href='/products?discount=true']");
  const allMenuItem = document.querySelector(".category_menu.all");
  const top3ProductsContainer = document.getElementById("top3-product-list");

  const itemsPerPage = 6;
  let currentPage = 1;

  // ✅ 인기 Top 3 상품
  function renderTop3Products(top3Products) {
    console.log("Top 3 Products:", top3Products); // 디버깅 로그
    if (!top3ProductsContainer) return;
    top3ProductsContainer.innerHTML = "";

    const products = top3Products || [];
    if (products.length > 0) {
      products.forEach(product => {
        const li = document.createElement("li");
        li.className = "product-item";
        li.innerHTML = `
                    <a href="/products/${product.productId}">
                        <div class="product-images">
                            <img src="${product.productImages[0]}" alt="상품 이미지">
                            <p><span>${product.productName}</span></p>
                        </div>
                        <div class="product-price">
                            ${product.discountRate > 0
            ? `<p>
                                    <span style="text-decoration: line-through;">${product.productPrice.toLocaleString()}원</span>
                                    <span style="color: red; font-weight: bold;">${product.discountedPrice.toLocaleString()}원</span>
                                    <span>(${product.discountRate}% 할인)</span>
                                </p>`
            : `<p><span>${product.productPrice.toLocaleString()}원</span></p>`
        }
                        </div>
                    </a>
                    <button class="bookmark-icon ${product.bookmarked ? 'bookmarked' : ''}" data-product-id="${product.productId}">
                        <i class="fas fa-bookmark"></i>
                    </button>
                `;
        top3ProductsContainer.appendChild(li);
      });
      attachBookmarkEvents(); // 이벤트 리스너 재등록
    } else {
      top3ProductsContainer.innerHTML = "<li><p>인기 상품이 없습니다.</p></li>";
    }
  }

  function refreshTop3Products() {
    fetch("/products/top3", { credentials: 'include' })
        .then(res => {
          console.log("Top 3 fetch response status:", res.status); // 디버깅 로그
          if (!res.ok) {
            throw new Error("Top 3 상품을 불러오는 데 실패했습니다: " + res.status);
          }
          return res.json();
        })
        .then(data => {
          console.log("Top 3 products data:", data); // 디버깅 로그
          renderTop3Products(data);
        })
        .catch(err => {
          console.error("Top 3 상품 로드 오류:", err.message);
          top3ProductsContainer.innerHTML = "<li><p>인기 상품을 불러오는 데 실패했습니다.</p></li>";
        });
  }

  // 초기 Top3 렌더링
  renderTop3Products(top3ProductsFromServer || []); // null 방지

  // ✅ 검색 기능
  if (searchInput) {
    searchInput.addEventListener("input", function () {
      const query = searchInput.value.trim().toLowerCase();
      if (query === "") {
        searchResultsWrap.style.display = "none";
        productsWrap.style.display = "block";
        pagination.style.display = "none";
        return;
      }

      const filtered = allProducts.filter(product =>
          product.productName.toLowerCase().includes(query)
      );

      currentPage = 1;
      renderProducts(filtered);
    });
  }

  function renderProducts(productArray) {
    searchResults.innerHTML = "";
    pagination.innerHTML = "";

    if (productArray.length === 0) {
      searchResults.innerHTML = "<p>검색 결과가 없습니다.</p>";
      productsWrap.style.display = "none";
      searchResultsWrap.style.display = "block";
      pagination.style.display = "none";
      return;
    }

    const start = (currentPage - 1) * itemsPerPage;
    const paginated = productArray.slice(start, start + itemsPerPage);

    paginated.forEach(product => {
      const li = document.createElement("li");
      li.innerHTML = `
                <img src="${product.image}" alt="${product.productName}" />
                <p>${product.productName}</p>
            `;
      searchResults.appendChild(li);
    });

    searchResultsWrap.style.display = "block";
    productsWrap.style.display = "none";
    pagination.style.display = "block";
    renderPagination(productArray);
  }

  function renderPagination(productArray) {
    pagination.innerHTML = "";
    const totalPages = Math.ceil(productArray.length / itemsPerPage);

    const createButton = (text, disabled, callback) => {
      const btn = document.createElement("button");
      btn.textContent = text;
      btn.disabled = disabled;
      btn.addEventListener("click", callback);
      return btn;
    };

    pagination.appendChild(createButton("이전", currentPage === 1, () => {
      currentPage--;
      renderProducts(productArray);
    }));

    for (let i = 1; i <= totalPages; i++) {
      const btn = createButton(i, false, () => {
        currentPage = i;
        renderProducts(productArray);
      });
      if (i === currentPage) btn.classList.add("active");
      pagination.appendChild(btn);
    }

    pagination.appendChild(createButton("다음", currentPage === totalPages, () => {
      currentPage++;
      renderProducts(productArray);
    }));
  }

  // ✅ 카테고리 필터
  const allCategoryItems = [...categoryMenus, ...categoryWraps];

  allCategoryItems.forEach(item => {
    item.addEventListener("click", event => {
      event.preventDefault();
      const category = item.dataset.category;
      activateCategory(item);
      filterProductsByCategory(category);
    });
  });

  discountButtons.forEach(button => {
    button.parentElement.addEventListener("click", (event) => {
      event.preventDefault();
      activateCategory(button.parentElement);
      window.location.href = "/products?discount=true";
    });
  });

  allMenuItem?.addEventListener("click", (event) => {
    event.preventDefault();
    activateCategory(allMenuItem);
    window.location.href = "/products";
  });

  function filterProductsByCategory(categoryId) {
    const params = new URLSearchParams(window.location.search);
    const isDiscount = params.get("discount") === "true";
    let url = `/products?category=${categoryId}`;
    if (isDiscount) url += "&discount=true";
    window.location.href = url;
  }

  function activateCategory(selected) {
    allCategoryItems.forEach(item => item.classList.remove("active"));
    selected.classList.add("active");
  }

  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get("discount") === "true") {
    discountButtons.forEach(button => button.parentElement.classList.add("active"));
  }

  const selectedCategoryId = urlParams.get("category");
  if (selectedCategoryId) {
    const selectedEl = document.querySelector(`[data-category="${selectedCategoryId}"]`);
    selectedEl && activateCategory(selectedEl);
  }

  // ✅ 북마크 기능
  function attachBookmarkEvents() {
    document.querySelectorAll(".bookmark-icon").forEach(button => {
      // 기존 이벤트 리스너 제거 (중복 방지)
      button.removeEventListener("click", button._bookmarkHandler);
      button._bookmarkHandler = function (event) {
        event.preventDefault();
        const productId = this.getAttribute("data-product-id");
        let isBookmarked = this.classList.contains("bookmarked");

        const url = isBookmarked ? `/bookmark/remove/${productId}` : "/bookmark/add";
        const method = isBookmarked ? "DELETE" : "POST";

        const options = {
          method,
          credentials: 'include'
        };

        if (!isBookmarked) {
          options.headers = { "Content-Type": "application/x-www-form-urlencoded" };
          options.body = `productId=${productId}`;
        }

        console.log(`Sending ${method} request to ${url} for productId=${productId}, isBookmarked=${isBookmarked}`);

        fetch(url, options)
            .then(res => {
              console.log("Response status:", res.status);
              if (!res.ok) {
                return res.json().then(data => { throw new Error(data.error || "요청 실패: " + res.status); });
              }
              return res.json();
            })
            .then(data => {
              console.log("Response data:", data);
              if (data.success) {
                this.classList.toggle("bookmarked", !isBookmarked);
                refreshTop3Products();
              } else {
                if (data.error === "이미 추가된 북마크입니다.") {
                  this.classList.add("bookmarked");
                  isBookmarked = true;
                  alert(data.error);
                } else if (data.error === "로그인이 필요합니다.") {
                  alert(data.error);
                  window.location.href = "/user/login";
                } else {
                  alert(data.error || "북마크 처리 실패");
                }
              }
            })
            .catch(err => {
              console.error("북마크 오류:", err.message);
              alert("북마크 처리 중 오류 발생: " + err.message);
            });
      };
      button.addEventListener("click", button._bookmarkHandler);
    });
  }

  attachBookmarkEvents();
});