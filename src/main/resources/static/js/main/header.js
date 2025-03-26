window.addEventListener("DOMContentLoaded", function () {
  const headerContainer = document.getElementById("header");
  if (!headerContainer) {
    console.error("🚨 #header 요소를 찾을 수 없습니다!");
    return;
  }
  console.log("📢 Header loaded:", headerContainer);
  addMenuEventListeners();
  initSubMenuToggle();
  initPopupEvent(); // 팝업 이벤트 초기화 함수 추가
});

function addMenuEventListeners() {
  const navibar = document.querySelector(".menu_icon");
  const menu = document.querySelector(".menu");
  const modalBackground = document.querySelector(".background");

  if (!navibar || !menu) {
    console.warn("🚨 필수 메뉴 요소(.menu_icon, .menu)를 찾을 수 없습니다!");
    return;
  }

  navibar.addEventListener("click", function () {
    menu.classList.toggle("open");
    navibar.classList.toggle("active");
    if (modalBackground) modalBackground.classList.toggle("active");
  });

  if (modalBackground) {
    modalBackground.addEventListener("click", function () {
      menu.classList.remove("open");
      navibar.classList.remove("active");
      modalBackground.classList.remove("active");
    });
  }
}

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

// 팝업 이미지 클릭 이벤트 추가
function initPopupEvent() {
  const popupImage = document.querySelector(".popup-banner");
  if (!popupImage) {
    console.warn("🚨 .popup-banner 요소를 찾을 수 없습니다!");
    return;
  }

  popupImage.addEventListener("click", function (event) {
    event.stopPropagation(); // 이벤트 전파 방지 (팝업 닫기와 충돌 방지)
    window.location.href = "/popup/event"; // /popup/event로 이동
  });
}