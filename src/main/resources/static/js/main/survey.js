// 기본정보 화면 전환
const basicNextBtn1 = document.querySelector("#basicinfo_page1 .next_wrap");
const basicNextBtn2 = document.querySelector("#basicinfo_page2 .next_wrap");
const basicNextBtn3 = document.querySelector("#basicinfo_page3 .next_wrap");
const basicNextBtn4 = document.querySelector("#basicinfo_page4 .next_wrap");
const basicNextBtn5 = document.querySelector("#basicinfo_page5 .next_wrap");
const basicNextBtn6 = document.querySelector("#basicinfo_page6 .next_wrap");

// 1페이지 -> 2페이지
basicNextBtn1.addEventListener("click", () => {
  document.getElementById("basicinfo_page1").classList.remove("active");
  document.getElementById("basicinfo_page2").classList.add("active");
});

// 2페이지 -> 3페이지
basicNextBtn2.addEventListener("click", () => {
  document.getElementById("basicinfo_page2").classList.remove("active");
  document.getElementById("basicinfo_page3").classList.add("active");
});

// 3번째 페이지 -> 4번째 페이지
basicNextBtn3.addEventListener("click", () => {
  document.getElementById("basicinfo_page3").classList.remove("active");
  document.getElementById("basicinfo_page4").classList.add("active");
});

// 4번째 페이지 -> 5번째 페이지
basicNextBtn4.addEventListener("click", () => {
  document.getElementById("basicinfo_page4").classList.remove("active");
  document.getElementById("basicinfo_page5").classList.add("active");
});

// 5번째 페이지 -> 6번째 페이지
basicNextBtn5.addEventListener("click", () => {
  document.getElementById("basicinfo_page5").classList.remove("active");
  document.getElementById("basicinfo_page6").classList.add("active");
});

// 6번째 페이지 -> 프로틴 섭취목적 화면
basicNextBtn6.addEventListener("click", () => {
  document.getElementById("basicinfo_page6").classList.remove("active");
  document.getElementById("purpose_page1").classList.add("active");
});

//프로틴 섭취목적 화면 전환
const purposeNextBtn1 = document.querySelector("#purpose_page1 .next_wrap");
const purposeNextBtn2 = document.querySelector("#purpose_page2 .next_wrap");
const purposeNextBtn3 = document.querySelector("#purpose_page3 .next_wrap");

// 1페이지 -> 2페이지
purposeNextBtn1.addEventListener("click", () => {
  document.getElementById("purpose_page1").classList.remove("active");
  document.getElementById("purpose_page2").classList.add("active");
});

// 2페이지 -> 3페이지
purposeNextBtn2.addEventListener("click", () => {
  document.getElementById("purpose_page2").classList.remove("active");
  document.getElementById("purpose_page3").classList.add("active");
});

// 3페이지 -> 건강고민 화면 전환
purposeNextBtn3.addEventListener("click", () => {
  document.getElementById("purpose_page3").classList.remove("active");
  document.getElementById("concern_page1").classList.add("active");
});

// 건강고민 챕터 1
const healthChoice = document.querySelectorAll(
  "#concern_page1 .select_health_list"
);
const warningMessage = document.querySelector(".warning_message");
const warning = document.getElementById("warning");
const nextBtn = document.querySelector("#concern_page1 .next_wrap");

let maxSelections = 3; // 최대 선택 가능한 항목 수

let selectedItems = []; // 선택된 항목을 추적

healthChoice.forEach((li) => {
  li.addEventListener("click", () => {
    // 항목이 이미 선택되었을 경우, 해제
    if (li.classList.contains("select")) {
      li.classList.remove("select");
      selectedItems = selectedItems.filter((item) => item !== li); // 선택된 항목 배열에서 제거
    } else {
      // 3개 이하만 선택하도록 제한
      if (selectedItems.length < maxSelections) {
        li.classList.add("select");
        selectedItems.push(li); // 선택된 항목 배열에 추가
      }
    }

    // 3개 선택했을 때 경고창 띄우고 나머지 항목 비활성화
    if (selectedItems.length === maxSelections) {
      healthChoice.forEach((item) => {
        if (!item.classList.contains("select")) {
          item.classList.add("not_select");
          item.style.pointerEvents = "none"; // 비활성화
        }
      });
      warningMessage.classList.add("show");
      warning.style.display = "block"; // 경고문구 보이기
    } else {
      // 선택된 항목이 3개 미만일 때 비활성화 상태 해제
      healthChoice.forEach((item) => {
        item.classList.remove("not_select");
        item.style.pointerEvents = "auto"; // 활성화
      });
      warningMessage.classList.remove("show");
      warning.style.display = "none"; // 경고문구 숨기기
    }
  });
});

// "다음" 버튼 클릭 시 동작
nextBtn.addEventListener("click", () => {
  // 선택된 항목이 3개 이하일 때만 페이지 이동
  if (selectedItems.length <= maxSelections) {
    warningMessage.classList.remove("show");
    warning.style.display = "none"; // 경고문구 보이기

    document.getElementById("concern_page1").classList.remove("active");
    document.getElementById("concern_page2").classList.add("active");
  }
});

// 건강고민 챕터 2 (새로운 페이지로 이동한 후 항목 선택 가능하도록 설정)
const healthChoice2 = document.querySelectorAll(
  "#concern_page2 .select_health_list"
);
const nextBtn2 = document.querySelector("#concern_page2 .next_wrap");

// 2페이지에서 항목을 선택할 수 있도록
healthChoice2.forEach((li) => {
  li.addEventListener("click", () => {
    li.classList.toggle("select");
    // 경고 메시지는 챕터 2에서 없으므로 경고창을 숨김
    warningMessage.classList.remove("show");
    warning.style.display = "none";
  });
});

// page2 -> page3 "다음" 버튼 클릭 시 동작
nextBtn2.addEventListener("click", () => {
  const selectedItems2 = document.querySelectorAll("#concern_page2 .select");

  document.getElementById("concern_page2").classList.remove("active");
  document.getElementById("concern_page3").classList.add("active");
});

// 상단 이전 누르면 이전 페이지 돌아가기
const basicBeforeBtn = document.querySelector("#basicinfo_page1 .before_page");
const basicBeforeBtn2 = document.querySelector("#basicinfo_page2 .before_page");
const basicBeforeBtn3 = document.querySelector("#basicinfo_page3 .before_page");
const basicBeforeBtn4 = document.querySelector("#basicinfo_page4 .before_page");
const basicBeforeBtn5 = document.querySelector("#basicinfo_page5 .before_page");
const basicBeforeBtn6 = document.querySelector("#basicinfo_page6 .before_page");

const purposeBeforeBtn = document.querySelector("#purpose_page1 .before_page");
const purposeBeforeBtn2 = document.querySelector("#purpose_page2 .before_page");
const purposeBeforeBtn3 = document.querySelector("#purpose_page3 .before_page");

const beforeBtn = document.querySelector("#concern_page1 .before_page");
const beforeBtn2 = document.querySelector("#concern_page2 .before_page");
const beforeBtn3 = document.querySelector("#concern_page3 .before_page");

basicBeforeBtn.addEventListener("click", () => {
  window.location = "/survey_start.html";
});

basicBeforeBtn2.addEventListener("click", () => {
  document.getElementById("basicinfo_page1").classList.add("active");
  document.getElementById("basicinfo_page2").classList.remove("active");
});
basicBeforeBtn3.addEventListener("click", () => {
  document.getElementById("basicinfo_page2").classList.add("active");
  document.getElementById("basicinfo_page3").classList.remove("active");
});
basicBeforeBtn4.addEventListener("click", () => {
  document.getElementById("basicinfo_page3").classList.add("active");
  document.getElementById("basicinfo_page4").classList.remove("active");
});
basicBeforeBtn5.addEventListener("click", () => {
  document.getElementById("basicinfo_page4").classList.add("active");
  document.getElementById("basicinfo_page5").classList.remove("active");
});
basicBeforeBtn6.addEventListener("click", () => {
  document.getElementById("basicinfo_page5").classList.add("active");
  document.getElementById("basicinfo_page6").classList.remove("active");
});

purposeBeforeBtn.addEventListener("click", () => {
  document.getElementById("basicinfo_page6").classList.add("active");
  document.getElementById("purpose_page1").classList.remove("active");
});

purposeBeforeBtn2.addEventListener("click", () => {
  document.getElementById("purpose_page1").classList.add("active");
  document.getElementById("purpose_page2").classList.remove("active");
});

purposeBeforeBtn3.addEventListener("click", () => {
  document.getElementById("purpose_page2").classList.add("active");
  document.getElementById("purpose_page3").classList.remove("active");
});

beforeBtn.addEventListener("click", () => {
  document.getElementById("purpose_page3").classList.add("active");
  document.getElementById("concern_page1").classList.remove("active");
});

beforeBtn2.addEventListener("click", () => {
  document.getElementById("concern_page1").classList.add("active");
  document.getElementById("concern_page2").classList.remove("active");
});

beforeBtn3.addEventListener("click", () => {
  document.getElementById("concern_page2").classList.add("active");
  document.getElementById("concern_page3").classList.remove("active");
});

// close 버튼 클릭시 
document.addEventListener("DOMContentLoaded", function () {
  // 요소 가져오기
  const closeBtn = document.querySelector(".close_btn");
  const closeBtn2 = document.querySelector(".close_btn2");
  const popupBg = document.querySelector(".popup_bg");
  const endBtn = document.querySelector(".end_btn");
  const keepBtn = document.querySelector(".keep_btn");

  // close 버튼 클릭 시 팝업 보이기/숨기기 (토글)
  closeBtn.addEventListener("click", function () {
    if (popupBg.classList.contains("active")) {
      popupBg.classList.remove("active");
    } else {
      popupBg.classList.add("active");
    }
  });


  // 종료 버튼 클릭 시 페이지 이동
  endBtn.addEventListener("click", function () {
    window.location = "/Propick/main.html";
  });

  // 계속 설문하기 버튼 클릭 시 팝업 닫기
  keepBtn.addEventListener("click", function () {
    popupBg.classList.remove("active");
  });
});

// 설문 결과
document.addEventListener("DOMContentLoaded", function () {
  // 요소 가져오기
  const closeBtn = document.querySelector(".close_btn3");
  const popupBg = document.querySelector(".popup_bg");
  const endBtn = document.querySelector(".end_btn");
  const keepBtn = document.querySelector(".keep_btn");
  const stars = document.querySelectorAll(".star");

  // 별점 변수
  let rating = 0;

  // 별 클릭 이벤트
  stars.forEach(star => {
      star.addEventListener("click", function () {
          rating = parseInt(this.getAttribute("data-value")); // 클릭한 별의 값 저장
          updateStars(rating); // 별 색상 업데이트
      });

      // 마우스 오버 시 미리보기 효과 (선택 사항)
      star.addEventListener("mouseover", function () {
          const hoverValue = parseInt(this.getAttribute("data-value"));
          updateStars(hoverValue);
      });

      // 마우스가 떠나면 원래 별점으로 복원 (선택 사항)
      star.addEventListener("mouseout", function () {
          updateStars(rating);
      });
  });

  // 별 색상 업데이트 함수
  function updateStars(value) {
      stars.forEach(star => {
          const starValue = parseInt(star.getAttribute("data-value"));
          if (starValue <= value) {
              star.classList.add("filled");
          } else {
              star.classList.remove("filled");
          }
      });
  }

  // close 버튼 클릭 시 팝업 보이기/숨기기 (토글)
  closeBtn.addEventListener("click", function () {
      if (popupBg.classList.contains("active")) {
          popupBg.classList.remove("active");
      } else {
          popupBg.classList.add("active");
      }
  });

  // 종료 버튼 클릭 시 페이지 이동
  endBtn.addEventListener("click", function () {
      console.log("선택된 별점:", rating); // 별점 확인 (필요 시 서버로 전송)
      window.location = "/main.html";
  });

  // 계속 설문하기 버튼 클릭 시 팝업 닫기
  keepBtn.addEventListener("click", function () {
      console.log("선택된 별점:", rating); // 별점 확인 (필요 시 서버로 전송)
      popupBg.classList.remove("active");
  });
});