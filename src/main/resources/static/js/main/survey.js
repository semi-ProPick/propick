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

// ✅ 건강 고민 항목 최대 3개까지 선택 제한
document.querySelectorAll('.select_health_list').forEach(item => {
  item.addEventListener('click', () => {
    const selectedItems = document.querySelectorAll('.select_health_list.selected');
    if (item.classList.contains('selected')) {
      item.classList.remove('selected');
    } else {
      if (selectedItems.length >= 3) {
        showWarning("최대 3개까지 선택할 수 있어요.");
        return;
      }
      item.classList.add('selected');
    }
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
backMap.forEach(([from, to]) => {
  const btn = document.querySelector(`#${from} .before_page`);
  if (btn) {
    btn.addEventListener("click", () => {
      document.getElementById(from).classList.remove("active");
      document.getElementById(to).classList.add("active");
    });
  }
});

document.addEventListener("keydown", function (e) {
  if (e.key === "Enter") {
    const currentPage = document.querySelector(".contents_wrap.active");
    const nextButton = currentPage?.querySelector(".next_wrap");
    if (nextButton) nextButton.click();
  }
});

// ✅ 세부 질문 로직용 데이터
const healthDetailQuestions = {
  "소화, 장": {
    title: `
      <div class="question-title-with-img">
        <img src="/images/main/bowel.png" alt="소화, 장" />
        <span>소화 문제</span>
      </div>
    `,
    description: "해당되는 소화 증상을 선택해주세요.",
    options: [
      "소화가 잘 안 돼요",
      "유제품 섭취 후 소화불량이 있어요",
      "설사가 자주 발생해요",
      "변비가 자주 있어요"
    ]
  },
  "피부 질환": {
    title: `
      <div class="question-title-with-img">
        <img src="/images/main/skin_disease.png" alt="피부 질환" />
        <span>피부 문제</span>
      </div>
    `,
    description: "피부와 관련된 증상을 선택해주세요.",
    options: [
      "여드름이 자주 생겨요",
      "피부에 발진이나 가려움증이 있어요",
      "유제품 섭취 후 피부 반응이 있어요",
      "콩 제품 섭취 후 피부 반응이 있어요",
      "견과류나 첨가물에 알레르기가 있어요"
    ]
  },
  "신장 부담": {
    title: `
      <div class="question-title-with-img">
        <img src="/images/main/kidney.png" alt="신장 부담" />
        <span>신장 건강</span>
      </div>
    `,
    description: "신장과 관련된 증상을 선택해주세요.",
    options: [
      "신장 질환(신부전, 결석 등) 진단을 받은 적 있어요",
      "소변량이 줄거나 부종이 자주 생겨요",
      "소변에 거품이 많아요(단백뇨 의심)",
      "나트륨 섭취를 제한하고 있어요"
    ]
  },
  "수면 장애": {
    title: `
      <div class="question-title-with-img">
        <img src="/images/main/sleep_disorder.png" alt="수면 장애" />
        <span>수면 문제</span>
      </div>
    `,
    description: "수면과 관련된 증상을 선택해주세요.",
    options: [
      "잠들기 어려워요",
      "자주 깨거나 얕게 자요",
      "저녁 늦게 먹으면 잠이 안 와요"
    ]
  },
  "관절(골다공증)": {
    title: `
      <div class="question-title-with-img">
        <img src="/images/main/joint.png" alt="관절 건강" />
        <span>관절 및 뼈 건강</span>
      </div>
    `,
    description: "관절이나 뼈 건강과 관련된 증상을 선택해주세요.",
    options: [
      "골다공증 또는 관절염 진단을 받은 적 있어요",
      "관절 통증이 자주 있어요"
    ]
  },
  "간": {
    title: `
      <div class="question-title-with-img">
        <img src="/images/main/liver.png" alt="간 건강" />
        <span>간 건강</span>
      </div>
    `,
    description: "간 건강과 관련된 증상을 선택해주세요.",
    options: [
      "지방간, 간염 등 간 질환을 진단받은 적 있어요",
      "피로감이 자주 느껴져요"
    ]
  },
  "혈관, 혈액순환": {
    title: `
      <div class="question-title-with-img">
        <img src="/images/main/blood.png" alt="혈관 건강" />
        <span>혈관 및 혈액순환 문제</span>
      </div>
    `,
    description: "혈관 및 혈액순환과 관련된 증상을 선택해주세요.",
    options: [
      "고혈압 또는 심혈관 질환 진단을 받은 적 있어요",
      "손발이 차갑거나 혈액순환이 잘 안 돼요",
      "콜레스테롤이나 중성지방 수치가 높아요"
    ]
  }
};

// ✅ 세부 질문 표시 흐름
let selectedHealthConcerns = [];
let currentDetailIndex = 0;

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
  const detail = healthDetailQuestions[concern];

  if (!detail) {
    showDetailQuestion(index + 1);
    return;
  }

  document.getElementById("concern_detail_page").style.display = "block";

  // ✅ 이미지 + 제목 삽입
  const titleContainer = document.getElementById("health_detail_title");
  titleContainer.innerHTML = detail.title;

  document.getElementById("detail_question_description").innerText = detail.description;

  const container = document.getElementById("detail_options_container");
  container.innerHTML = "";
  detail.options.forEach(option => {
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

  const hasValidDetail = selectedHealthConcerns.some(concern => healthDetailQuestions[concern]);
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

// ✅ 결과 저장 및 이동
function collectSurveyInput() {
  const name = document.getElementById("nametext").value.trim();
  const gender = document.querySelector("#basicinfo_page2 .select_purpose_list.selected p")?.innerText;
  const age = parseInt(document.getElementById("agetext").value.trim(), 10);
  const height = parseFloat(document.getElementById("heighttext").value.trim());
  const weight = parseFloat(document.getElementById("weighttext").value.trim());
  const purpose = document.querySelector("#purpose_page1 .select_purpose_list.selected p")?.innerText;
  const workoutFreq = document.querySelector("#purpose_page2 .select_purpose_list.selected p")?.innerText;

  const concerns = Array.from(document.querySelectorAll(
      "#concern_page1 .select_health_list.select p, #concern_page2 .select_health_list.select p"
  )).map(el => el.innerText.trim());

  const inputData = { name, gender, age, heightCm: height, weightKg: weight, purpose, workoutFreq, healthConcerns: concerns };
  console.log("설문 결과:", inputData);
  localStorage.setItem("surveyInput", JSON.stringify(inputData));
  window.location.href = "/result";
}

// ✅ 페이지 이동 함수
function nextPage(fromId, toId) {
  document.getElementById(fromId).classList.remove("active");
  document.getElementById(toId).classList.add("active");
}