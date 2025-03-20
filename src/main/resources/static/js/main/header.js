// 헤더 로드 및 메뉴 초기화
window.addEventListener("DOMContentLoaded", function () {
  const headerContainer = document.getElementById("header");

  // 🚨 headerContainer가 존재하는지 확인
  if (!headerContainer) {
    console.error("🚨 #header 요소를 찾을 수 없습니다!");
    return; // header가 없으면 코드 실행 중단
  }

  fetch("/mainHeader.html")
    .then((response) => {
      console.log("📢 Fetch Response:", response);
      if (!response.ok) throw new Error(`HTTP 오류! 상태 코드: ${response.status}`);
      return response.text();
    })
    .then((data) => {
      console.log("📢 Fetch 성공! 데이터 로드 완료");
      console.log("📢 HTML 내용:", data);

      // 헤더 추가
      headerContainer.innerHTML = data;

      // 🚀 메뉴 클릭 시 동작
      addMenuEventListeners(); // 메뉴 이벤트 리스너 추가
      initSubMenuToggle(); // 하위 메뉴 토글 초기화
    })
    .catch((error) => console.error("🚨 Fetch 실패:", error));
});

// 메뉴 클릭 시 동작
function addMenuEventListeners() {
  const navibar = document.querySelector(".menu_icon");
  const menu = document.querySelector(".menu");
  const modalBackground = document.querySelector(".background");

  if (navibar && menu && modalBackground) {
    navibar.addEventListener("click", function () {
      menu.classList.toggle("open");
      navibar.classList.toggle("active");
      modalBackground.classList.toggle("active");
    });

    modalBackground.addEventListener("click", function () {
      menu.classList.remove("open");
      navibar.classList.remove("active");
      modalBackground.classList.remove("active");
    });
  }
}

// 소식, 게시판 클릭 시 하위 메뉴 토글
function initSubMenuToggle() {
  const submenuItems = document.querySelectorAll(".submenu > a");

  submenuItems.forEach(function (item) {
    item.addEventListener("click", function (e) {
      e.preventDefault();

      const subMenuList = this.nextElementSibling;

      if (subMenuList) {
        subMenuList.classList.toggle("active");
      }
    });
  });
}
