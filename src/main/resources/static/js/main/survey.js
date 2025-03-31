// 🔒 유효성 검사 및 경고 표시
function showWarning(message) {
  const warningBox = document.querySelector(".warning_message");
  const warningText = document.getElementById("warning");
  warningText.innerText = message;
  warningBox.classList.add("show");
  warningBox.scrollIntoView({ behavior: "smooth", block: "center" });
}
function hideWarning() {
  document.querySelector(".warning_message").classList.remove("show");
}

// ✅ 각 단계별 Next 버튼 - 유효성 검사 포함
document.querySelector("#basicinfo_page1 .next_wrap").addEventListener("click", () => {
  const name = document.getElementById("nametext").value.trim();
  if (name === "") return showWarning("답변을 입력해주세요.");
  hideWarning();

  // ✅ 입력한 이름을 user-name, user-name2 영역에 표시
  document.querySelectorAll("#user-name, #user-name2").forEach(el => {
    el.textContent = name;
  });

  nextPage("basicinfo_page1", "basicinfo_page2");
});


document.querySelector("#basicinfo_page2 .next_wrap").addEventListener("click", () => {
  const selected = document.querySelector("#basicinfo_page2 .select_purpose_list.selected");
  if (!selected) return showWarning("성별을 선택해주세요.");
  hideWarning();
  nextPage("basicinfo_page2", "basicinfo_page3");
});

document.querySelector("#basicinfo_page3 .next_wrap").addEventListener("click", () => {
  const age = document.getElementById("agetext").value.trim();
  if (!/^\d+$/.test(age)) return showWarning("숫자만 입력해주세요.");
  const a = parseInt(age);
  if (a < 10 || a > 110) return showWarning("10부터 110까지 입력 가능합니다.");
  hideWarning();
  nextPage("basicinfo_page3", "basicinfo_page4");
});

document.querySelector("#basicinfo_page4 .next_wrap").addEventListener("click", () => {
  const height = document.getElementById("heighttext").value.trim();
  if (!/^\d+$/.test(height)) return showWarning("숫자만 입력해주세요.");
  const h = parseInt(height);
  if (h < 100 || h > 250) return showWarning("100부터 250까지 입력가능합니다.");
  hideWarning();
  nextPage("basicinfo_page4", "basicinfo_page5");
});

document.querySelector("#basicinfo_page5 .next_wrap").addEventListener("click", () => {
  const weight = document.getElementById("weighttext").value.trim();
  if (!/^\d+$/.test(weight)) return showWarning("숫자만 입력해주세요.");
  const w = parseInt(weight);
  if (w < 30 || w > 190) return showWarning("30부터 190까지 입력가능합니다.");
  hideWarning();
  nextPage("basicinfo_page5", "basicinfo_page6");
});

document.querySelector("#basicinfo_page6 .next_wrap").addEventListener("click", () => {
  hideWarning();
  nextPage("basicinfo_page6", "purpose_page1");
});

document.querySelector("#purpose_page1 .next_wrap").addEventListener("click", () => {
  const selected = document.querySelector("#purpose_page1 .select_purpose_list.selected");
  if (!selected) return showWarning("섭취 목적을 선택해주세요.");
  hideWarning();
  nextPage("purpose_page1", "purpose_page2");
});

document.querySelector("#purpose_page2 .next_wrap").addEventListener("click", () => {
  const selected = document.querySelector("#purpose_page2 .select_purpose_list.selected");
  if (!selected) return showWarning("운동 빈도를 선택해주세요.");
  hideWarning();
  nextPage("purpose_page2", "purpose_page3");
});

document.querySelector("#purpose_page3 .next_wrap").addEventListener("click", () => {
  hideWarning();
  nextPage("purpose_page3", "concern_page1");
});

// ✅ 선택 항목 토글 (성별, 목적, 운동 빈도 등)
document.querySelectorAll(".select_purpose_list").forEach(item => {
  item.addEventListener("click", () => {
    const siblings = item.parentElement.querySelectorAll(".select_purpose_list");
    siblings.forEach(el => el.classList.remove("selected"));
    item.classList.add("selected");
  });
});

// ✅ 이전 버튼
const backMap = [
  ["basicinfo_page2", "basicinfo_page1"],
  ["basicinfo_page3", "basicinfo_page2"],
  ["basicinfo_page4", "basicinfo_page3"],
  ["basicinfo_page5", "basicinfo_page4"],
  ["basicinfo_page6", "basicinfo_page5"],
  ["purpose_page1", "basicinfo_page6"],
  ["purpose_page2", "purpose_page1"],
  ["purpose_page3", "purpose_page2"],
  ["concern_page1", "purpose_page3"],
  ["concern_page2", "concern_page1"],
  ["concern_page3", "concern_page2"]
];


// ✅ 건강 고민 API 기반 트리 구조 로딩
// ✅ 건강 고민 이미지 매핑
const healthConcernImages = {
  "소화, 장": "/images/main/digest.png",
  "피부 질환": "/images/main/skin_disease.png",
  "신장 부담": "/images/main/kidney.png",
  "수면 장애": "/images/main/sleep_disorder.png",
  "관절 건강": "/images/main/joint.png",
  "간 건강": "/images/main/liver.png",
  "혈관 건강": "/images/main/blood.png"
};

let healthConcernMap = {};
let selectedHealthConcerns = [];
let currentDetailIndex = 0;

// ✅ 건강 고민 로딩
async function loadHealthConcernsFromSurvey() {
  const res = await fetch("/api/surveys/1");
  const data = await res.json();
  const healthQuestion = data.questions.find(q => q.questionText.includes("건강 고민"));
  if (!healthQuestion) return;

  healthQuestion.options.forEach(option => {
    const parentText = option.optionText.trim();
    const children = option.childOptions?.map(o => o.optionText.trim()) || [];
    healthConcernMap[parentText] = children;
  });

  renderTopConcerns(Object.keys(healthConcernMap));
}

function renderTopConcerns(optionList) {
  const container = document.querySelector("#top_health_concern_container");
  container.innerHTML = "";

  optionList.forEach(text => {
    const li = document.createElement("li");
    li.className = "select_health_list";
    const imageSrc = healthConcernImages[text] || "/images/default.png";

    li.innerHTML = `
      <div class="health-option-box">
        <img src="${imageSrc}" alt="${text}" />
        <p>${text}</p>
      </div>
    `;

    li.addEventListener("click", () => {
      if (li.classList.contains("selected")) {
        li.classList.remove("selected");
      } else {
        const selectedItems = document.querySelectorAll(".select_health_list.selected");
        if (selectedItems.length >= 3) {
          showWarning("최대 3개까지 선택할 수 있어요.");
          return;
        }
        li.classList.add("selected");
      }
    });

    container.appendChild(li);
  });
}

function collectSelectedConcerns() {
  const selected = document.querySelectorAll("#concern_page1 .select_health_list.selected p");
  return Array.from(selected).map(el => el.innerText.trim());
}

function showDetailQuestion(index) {
  if (index >= selectedHealthConcerns.length) {
    document.getElementById("concern_detail_page").style.display = "none";
    document.getElementById("concern_page2").classList.remove("active");
    document.getElementById("concern_page3").classList.add("active");
    return;
  }

  const concern = selectedHealthConcerns[index];
  const children = healthConcernMap[concern];
  if (!children || children.length === 0) {
    showDetailQuestion(index + 1);
    return;
  }
  document.getElementById("concern_detail_page").style.display = "block";
  document.getElementById("concern_page2").classList.add("active");
  document.getElementById("health_detail_title").innerHTML = `
    <div class="question-title-with-img">
      <img src="${healthConcernImages[concern] || '/images/default.png'}" alt="${concern}" />
      <span>${concern} 관련 증상</span>
    </div>
  `;
  document.getElementById("detail_question_description").innerText = "해당되는 증상을 선택해주세요.";

  const container = document.getElementById("detail_options_container");
  container.innerHTML = "";
  children.forEach(option => {
    const li = document.createElement("li");
    li.className = "select_health_list";
    li.innerHTML = `<p>${option}</p>`;
    li.onclick = () => li.classList.toggle("select");
    container.appendChild(li);
  });
}
document.querySelector("#concern_page1 .next_wrap").addEventListener("click", () => {
  selectedHealthConcerns = collectSelectedConcerns();

  if (selectedHealthConcerns.length === 0) {
    showWarning("하나 이상 선택해주세요.");
    return;
  }

  const hasValidDetail = selectedHealthConcerns.some(concern => healthConcernMap[concern]?.length > 0);

  if (!hasValidDetail) {
    alert("선택된 고민에 대한 추가 질문이 없어 바로 결과 페이지로 이동합니다.");
    window.location.href = "/result";
    return;
  }

  hideWarning();
  nextPage("concern_page1", "concern_page2");
  currentDetailIndex = 0;
  showDetailQuestion(currentDetailIndex);
});


document.getElementById("detail_next_btn").addEventListener("click", () => {
  currentDetailIndex++;
  showDetailQuestion(currentDetailIndex);
});



// ✅ 페이지 로딩 시 실행
document.addEventListener("DOMContentLoaded", () => {
  loadHealthConcernsFromSurvey();
});

// ✅ Enter 키로 다음 페이지 이동 처리
document.addEventListener("keydown", function (e) {
  if (e.key === "Enter") {
    const currentPage = document.querySelector(".contents_wrap.active");
    const nextButton = currentPage?.querySelector(".next_wrap");
    if (nextButton) nextButton.click();
  }
});
// ✅ 팝업 처리
const popupBg = document.querySelector(".popup_bg");
document.querySelectorAll(".close_btn, .close_btn2").forEach(closeBtn => {
  closeBtn.addEventListener("click", () => {
    popupBg.classList.toggle("active");
  });
});
document.querySelector(".end_btn")?.addEventListener("click", () => {
  window.location.href = "/Propick/main.html";
});
document.querySelector(".keep_btn")?.addEventListener("click", () => {
  popupBg.classList.remove("active");
});

// ✅ 페이지 전환 함수
function nextPage(fromId, toId) {
  document.getElementById(fromId).classList.remove("active");
  document.getElementById(toId).classList.add("active");
}
// ✅ [이전] 버튼 동작
document.querySelectorAll(".before_page").forEach(button => {
  button.addEventListener("click", () => {
    const currentPageId = button.closest(".contents_wrap")?.id;
    const target = backMap.find(pair => pair[0] === currentPageId);
    if (target) {
      nextPage(target[0], target[1]);
    }
  });
});

// ✅ [닫기] 버튼 동작 (팝업 열기)
document.querySelectorAll(".close_btn").forEach(btn => {
  btn.addEventListener("click", () => {
    document.querySelector(".popup_bg").classList.add("active");
  });
});

// ✅ 결과 저장 및 로그인 여부 확인 후 이동
function collectSurveyInput() {
  // 1. 설문 입력값 수집
  const name = document.getElementById("nametext").value.trim();
  const gender = document.querySelector("#basicinfo_page2 .select_purpose_list.selected p")?.innerText;
  const age = parseInt(document.getElementById("agetext").value.trim(), 10);
  const height = parseFloat(document.getElementById("heighttext").value.trim());
  const weight = parseFloat(document.getElementById("weighttext").value.trim());
  const purpose = document.querySelector("#purpose_page1 .select_purpose_list.selected p")?.innerText;
  const workoutFreq = document.querySelector("#purpose_page2 .select_purpose_list.selected p")?.innerText;

  const concerns = Array.from(document.querySelectorAll(
      "#concern_page1 .select_health_list.selected p, #concern_page2 .select_health_list.select p"
  )).map(el => el.innerText.trim());

  // 2. 데이터 localStorage 저장
  const inputData = {
    name,
    gender,
    age,
    heightCm: height,
    weightKg: weight,
    purpose,
    workoutFreq,
    healthConcerns: concerns
  };
  localStorage.setItem("surveyInput", JSON.stringify(inputData));
  localStorage.setItem("userName", name); // 이름도 저장해서 결과 페이지에서 활용

  window.location.href = "/survey_result";

  // // 3. 로그인 여부 확인
  // const isLoggedIn = !!localStorage.getItem("userToken");
  // if (isLoggedIn) {
  //   // 로그인 O → 결과 페이지 이동
  //   window.location.href = "/survey_result";
  // } else {
  //   // 로그인 X → 로그인 페이지로 안내
  //   alert("설문 결과를 확인하려면 로그인 또는 회원가입이 필요합니다.");
  //   window.location.href = "/join";
  // }
  //
  // // 로그인 완료 후 -->join 페이지에 작성
  // const savedSurvey = localStorage.getItem("surveyInput");
  // if (savedSurvey) {
  //   window.location.href = "/survey_result";
  // }



}

