let surveyStructure = {};
let surveyAnswers = {};
let optionTextToIdMap = {};
let optionIdToQuestionId = {};
let pageQuestionList = [];
let selectedHealthConcerns = [];
let currentDetailIndex = 0;
const healthConcernMap = {};

const healthConcernImages = {
    "소화 장": "/images/main/digest.png",
    "피부 질환": "/images/main/skin_disease.png",
    "신장 부담": "/images/main/kidney.png",
    "수면 장애": "/images/main/sleep_disorder.png",
    "관절 건강": "/images/main/joint.png",
    "간 건강": "/images/main/liver.png",
    "혈관 건강": "/images/main/blood.png"
};

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

function nextPage(fromId, toId) {
    document.getElementById(fromId)?.classList.remove("active");
    document.getElementById(toId)?.classList.add("active");
}

function getOptionId(text) {
    return optionTextToIdMap[text.trim()] || null;
}

function getQuestionIdByOptionText(text) {
    const optionId = getOptionId(text);
    return optionId ? optionIdToQuestionId[optionId] : null;
}

function getHelpText(question) {
    const map = {
        1: "서비스 제공 시 고객님 확인을 위해 필요해요.😁",
        2: "성별에 따라 추천 프로틴이 달라질 수 있어요😁",
        3: "나이에 따라 추천 프로틴이 달라질 수 있어요😁",
        4: "프로틴 추천에 체질량 지수(BMI)를 활용합니다.😁",
        5: "프로틴 추천에 체질량 지수(BMI)를 활용합니다.😁"
    };
    return map[question.questionId] || "정확한 추천을 위해 필요해요";
}function renderSurveyQuestions(questionsData) {
    pageQuestionList = questionsData.map((_, idx) => `dynamic_page_${idx}`);

    questionsData.forEach((question, index) => {
        const pageId = pageQuestionList[index];
        const page = document.getElementById(pageId);
        if (!page) return;

        const title = page.querySelector(".contents_sub_title");
        const desc = page.querySelector(".contents_impo_title");
        const inputArea = page.querySelector(".dynamic_input_area");

        title.innerHTML = `<p>${question.questionText}</p>`;
        desc.innerHTML = `<p>${getHelpText(question)}</p>`;
        inputArea.innerHTML = "";

        if (question.questionType === "Single") {
            question.options.forEach(opt => {
                const btn = document.createElement("li");
                btn.className = "select_purpose_list";
                btn.innerHTML = `<p>${opt.optionText}</p>`;
                btn.onclick = () => {
                    inputArea.querySelectorAll("li").forEach(el => el.classList.remove("selected"));
                    btn.classList.add("selected");
                    surveyAnswers[question.questionId] = [opt.optionId];
                    console.log(`✅ [Single 선택 저장] Q${question.questionId}:`, opt.optionId);
                };
                inputArea.appendChild(btn);
            });

        } else if (question.questionType === "Multiple") {
            question.options.forEach(opt => {
                const btn = document.createElement("li");
                btn.className = "select_purpose_list";
                btn.dataset.optionId = opt.optionId;

                if (question.questionCode === "HEALTH_CONCERN" && healthConcernImages[opt.optionText]) {
                    btn.innerHTML = `
                        <img src="${healthConcernImages[opt.optionText]}" alt="${opt.optionText}" />
                        <p>${opt.optionText}</p>
                    `;
                } else {
                    btn.innerHTML = `<p>${opt.optionText}</p>`;
                }

                btn.onclick = () => {
                    btn.classList.toggle("selected");
                    const selectedLis = inputArea.querySelectorAll(".selected");
                    surveyAnswers[question.questionId] = Array.from(selectedLis).map(li => Number(li.dataset.optionId));
                };

                // ✅ 하위 증상 저장
                if (question.questionCode === "HEALTH_CONCERN") {
                    healthConcernMap[opt.optionText] = (opt.childOptions || []).map(child => ({
                        optionId: child.optionId,
                        optionText: child.optionText,
                        questionId: child.questionId
                    }));
                }

                inputArea.appendChild(btn);
            });

        } else if (question.questionType === "Text") {
            const insertWrapper = document.createElement("div");
            insertWrapper.className = "insert";

            const input = document.createElement("input");
            input.type = "text";

            const code = question.questionCode;
            if (code === "NAME") {
                input.id = "nametext";
                input.placeholder = "이름";
            } else if (code === "AGE") {
                input.id = "agetext";
                input.placeholder = "나이";
            } else if (code === "HEIGHT") {
                input.id = "heighttext";
                input.placeholder = "키";
            } else if (code === "WEIGHT") {
                input.id = "weighttext";
                input.placeholder = "몸무게";
            } else {
                input.placeholder = "답변을 입력해주세요";
            }

            input.className = "text_input";
            input.oninput = () => {
                const value = input.value.trim();

                if (["AGE", "HEIGHT", "WEIGHT"].includes(code)) {
                    const numericValue = Number(value);
                    surveyAnswers[question.questionId] = isNaN(numericValue) ? "" : numericValue;
                } else {
                    surveyAnswers[question.questionId] = value;
                    if (code === "NAME") {
                        input.id = "nametext";
                        input.placeholder = "이름";
                        localStorage.setItem("userName", value);
                    }
                }
            };

            insertWrapper.appendChild(input);

            if (code === "HEIGHT") {
                const unit = document.createElement("p");
                unit.className = "position";
                unit.innerText = "cm";
                insertWrapper.appendChild(unit);
            } else if (code === "WEIGHT") {
                const unit = document.createElement("p");
                unit.className = "position";
                unit.innerText = "kg";
                insertWrapper.appendChild(unit);
            }

            inputArea.appendChild(insertWrapper);
        }
    });

    // ✅ 다음 버튼 처리
    pageQuestionList.forEach((pageId, i) => {
        const nextBtn = document.querySelector(`#${pageId} .next_wrap`);
        if (nextBtn) {
            nextBtn.addEventListener("click", () => {
                const currentQuestion = questionsData[i];
                let answer = surveyAnswers[currentQuestion.questionId];
                const code = currentQuestion.questionCode;

                console.log(`[다음버튼 클릭] 질문코드: ${code}, 입력값:`, answer);

                if (!answer || (Array.isArray(answer) && answer.length === 0) || (typeof answer === "string" && answer.trim() === "")) {
                    return showWarning("답변을 입력해주세요");
                }

                if (["AGE", "HEIGHT", "WEIGHT"].includes(code)) {
                    answer = Number(answer);
                    if (isNaN(answer)) {
                        return showWarning("올바른 숫자를 입력해주세요");
                    }
                }

                if (code === "AGE" && (answer < 10 || answer > 110)) {
                    return showWarning("10부터 110까지 입력 가능합니다.");
                }

                if (code === "HEIGHT" && (answer < 100 || answer > 250)) {
                    return showWarning("100부터 250까지 입력 가능합니다.");
                }

                if (code === "WEIGHT" && (answer < 30 || answer > 190)) {
                    return showWarning("30부터 190까지 입력 가능합니다.");
                }

                // ✅ HEALTH_CONCERN → 하위 질문 시작
                if (code === "HEALTH_CONCERN") {
                    selectedHealthConcerns = currentQuestion.options
                        .filter(opt => surveyAnswers[currentQuestion.questionId]?.includes(opt.optionId))
                        .map(opt => opt.optionText);

                    const hasDetail = selectedHealthConcerns.some(
                        concern => healthConcernMap[concern] && healthConcernMap[concern].length > 0
                    );

                    if (!hasDetail) {
                        return nextPage(pageId, "concern_page3");
                    }

                    currentDetailIndex = 0;
                    showDetailQuestion(currentDetailIndex); // 순차 출력 시작
                    return;
                }

                hideWarning();
                const nextPageId = pageQuestionList[i + 1] || "concern_page3";
                nextPage(pageId, nextPageId);
            });
        }
    });
}

function showDetailQuestion(index) {
    if (index >= selectedHealthConcerns.length) {
        return nextPage("concern_detail_page", "concern_page3");
    }

    const concern = selectedHealthConcerns[index];
    const children = healthConcernMap[concern];

    if (!children || children.length === 0) {
        return showDetailQuestion(index + 1); // 다음 고민으로
    }

    document.querySelectorAll(".contents_wrap").forEach(p => p.classList.remove("active"));
    document.getElementById("concern_detail_page").classList.add("active");

    document.getElementById("health_detail_title").innerHTML = `
        <div class="question-title-with-img">
            <img src="${healthConcernImages[concern] || '/images/default.png'}" alt="${concern}" />
            <span>${concern} 관련 증상</span>
        </div>
    `;
    document.getElementById("detail_question_description").innerText = "해당되는 증상을 선택해주세요.";

    const container = document.getElementById("detail_options_container");
    container.innerHTML = "";

    children.forEach(child => {
        const li = document.createElement("li");
        li.className = "select_health_list";
        li.dataset.optionId = child.optionId;
        li.dataset.questionId = child.questionId;
        li.innerHTML = `<p>${child.optionText}</p>`;
        li.addEventListener("click", () => li.classList.toggle("selected"));
        container.appendChild(li);
    });

    document.getElementById("detail_next_btn").onclick = () => {
        const selected = container.querySelectorAll(".selected");
        if (selected.length === 0) {
            return showWarning("최소 1개 이상 선택해주세요.");
        }

        selected.forEach(li => {
            const qid = Number(li.dataset.questionId);
            if (!surveyAnswers[qid]) surveyAnswers[qid] = [];
            surveyAnswers[qid].push(Number(li.dataset.optionId));
        });

        hideWarning();
        currentDetailIndex++;
        showDetailQuestion(currentDetailIndex); // 다음 고민의 하위 질문 보여줌
    };
    // ✅ concern_detail_page 내 이전 버튼 처리
    const detailBeforeBtn = document.querySelector("#concern_detail_page .before_page");
    if (detailBeforeBtn) {
        detailBeforeBtn.onclick = () => {
            console.log("하위 증상 - 이전 버튼 클릭됨");
            if (currentDetailIndex > 0) {
                currentDetailIndex--;
                showDetailQuestion(currentDetailIndex);
            } else {
                // HEALTH_CONCERN 질문 페이지로 이동
                const healthPageId = pageQuestionList[pageQuestionList.length - 2]; // 보통 마지막은 concern_detail_page
                nextPage("concern_detail_page", healthPageId);
            }
        };
    }

    // ✅ concern_detail_page 내 닫기 버튼 처리
    const detailCloseBtn = document.querySelector("#concern_detail_page .close_btn");
    if (detailCloseBtn) {
        detailCloseBtn.onclick = () => {
            console.log("하위 증상 - 닫기 버튼 클릭됨");
            document.querySelector(".popup_bg").style.display = "flex";
        };
    }
}

function generateDynamicPages(count) {
    const wrap = document.getElementById("wrap");
    for (let i = 0; i < count; i++) {
        const page = document.createElement("div");
        page.className = "contents_wrap";
        page.id = `dynamic_page_${i}`;
        page.innerHTML = `
            <header>
                <div class="before_page">이전</div>
                <div class="contents_title"></div>
                <div class="close_btn"></div>
            </header>
            <div class="header_line">
                <ul>
                    <li class="header_line_first"></li>
                    <li class="header_line_second"></li>
                    <li class="header_line_third"></li>
                </ul>
            </div>
            <div class="contents_sub_title"></div>
            <div class="contents_impo_title"></div>
            <div class="dynamic_input_area"></div>
            <div class="next_wrap"><p>다음</p></div>
        `;
        wrap.insertBefore(page, document.getElementById("concern_page3"));
    }
}


function collectSurveyAnswers() {
    const answers = [];

    for (const [questionId, answer] of Object.entries(surveyAnswers)) {
        if (Array.isArray(answer)) {
            answers.push({ questionId: Number(questionId), selectedOptionIds: answer });
        } else {
            answers.push({ questionId: Number(questionId), freeTextAnswer: answer });
        }
    }

    return { surveyId: 1, answers };
}
["submit_btn", "submit_btn2"].forEach(id => {
    const btn = document.getElementById(id);
    if (btn) {
        btn.addEventListener("click", async () => {
            const inputData = collectSurveyAnswers();

            localStorage.setItem("surveyData", JSON.stringify(inputData));
            localStorage.removeItem("surveyResponseId");

            try {
                await fetch("/api/temp-survey", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(inputData)
                });
                console.log("✅ 세션에 설문 응답 임시 저장 성공");
            } catch (err) {
                console.error(" 세션 저장 실패:", err);
            }

            // 로그인 상태 확인

            try {
                const res = await fetch("/user/me", {
                    credentials: 'include'
                });
                if (res.ok) {
                    window.location.href = "/survey_result";
                } else {
                    window.location.href = "/user/login?redirect=/survey_result";
                }
            } catch (err) {
                console.error("로그인 상태 확인 실패:", err);
                window.location.href = "/user/login?redirect=/survey_result";
            }

        });
    }
});


// 페이지 로딩 시 설문 구조 및 option 매핑 초기화
window.addEventListener("DOMContentLoaded", async () => {
    try {
        const res = await fetch("/api/surveys/1");
        const data = await res.json();
        surveyStructure = data;

        generateDynamicPages(data.questions.length);


        // 옵션 매핑 초기화
        data.questions.forEach(question => {
            console.log("로드된 질문 ID:", question.questionId);
            question.options.forEach(option => {
                optionTextToIdMap[option.optionText.trim()] = option.optionId;
                optionIdToQuestionId[option.optionId] = question.questionId;
                (option.childOptions || []).forEach(child => {
                    optionTextToIdMap[child.optionText.trim()] = child.optionId;
                    optionIdToQuestionId[child.optionId] = question.questionId;
                });
            });
        });
        renderSurveyQuestions(data.questions);
        pageQuestionList.push("concern_detail_page");
        setupNavigationEvents();
    } catch (err) {
        console.error("설문지 로딩 실패:", err);
    }
});
// ✅ Enter 키로 다음 페이지 이동 처리
document.addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
        const currentPage = document.querySelector(".contents_wrap.active");
        if (!currentPage) {
            console.log("현재 활성화된 페이지를 찾을 수 없음");
            return;
        }

        const nextButton = currentPage.querySelector(".next_wrap");
        if (!nextButton) {
            console.log("다음 버튼을 찾을 수 없음");
            return;
        }

        console.log("Enter 키 입력됨, 다음 버튼 클릭");
        nextButton.click();
    }
});
function setupNavigationEvents() {
    document.querySelectorAll(".before_page").forEach((btn, idx) => {
        btn.addEventListener("click", () => {
            console.log(`이전 버튼 클릭됨 - idx: ${idx}`);
            if (idx > 0) {
                const currentPage = document.getElementById(pageQuestionList[idx]);
                const prevPage = document.getElementById(pageQuestionList[idx - 1]);
                if (currentPage && prevPage) {
                    currentPage.classList.remove("active");
                    prevPage.classList.add("active");
                }
            }
        });
    });

    document.querySelectorAll(".close_btn").forEach(btn => {
        btn.addEventListener("click", () => {
            console.log("닫기 버튼 클릭됨");
            document.querySelector(".popup_bg").style.display = "flex";
        });
    });

    document.querySelector(".end_btn")?.addEventListener("click", () => {
        console.log("설문 종료 버튼 클릭됨");
        window.location.href = "/";
    });

    document.querySelector(".keep_btn")?.addEventListener("click", () => {
        console.log("계속하기 버튼 클릭됨");
        document.querySelector(".popup_bg").style.display = "none";
    });
}

