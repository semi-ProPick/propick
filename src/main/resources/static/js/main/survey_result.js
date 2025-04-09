console.log("result.js loaded. current page:", window.location.pathname);

// 유저 정보 출력
function updateUserInfo({ gender, age, bmi, bmiStatus }) {
    const infoText = document.getElementById("user_info_text");
    const genderText = gender === "FEMALE" ? "여성" : gender === "MALE" ? "남성" : "기타";
    if (infoText) {
        infoText.innerText = `성별: ${genderText}   |   나이: ${age}세   |   BMI: ${bmi} (${bmiStatus})`;
    }
}function renderHealthConditionBars(healthData) {
    const ctx = document.getElementById("health_conditions_chart").getContext("2d");

    if (window.childChart) window.childChart.destroy();

    // 실제로 선택된 상위 건강 항목(label)과 점수(data)만 추출
    const selectedLabels = Object.keys(healthData);         // 예: ["소화 장", "피부 질환", "수면 장애"]
    const selectedScores = Object.values(healthData);       // 예: [70, 100, 30]

    window.childChart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: selectedLabels,
            datasets: [{
                label: "하위 증상별 점수",
                data: selectedScores,
                backgroundColor: "rgba(255, 159, 64, 0.4)",
                borderColor: "#FF9F40",
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            indexAxis: "y", // 수평 막대 차트
            plugins: {
                title: {
                    display: true,
                    text: "건강상태 점수 분석",
                    font: {
                        size: 18
                    }
                },
                legend: {
                    display: false
                }
            },
            scales: {
                x: {
                    min: 0,
                    max: 100,
                    ticks: {
                        stepSize: 20
                    }
                },
                y: {
                    ticks: {
                        font: {
                            size: 12
                        }
                    }
                }
            }
        }
    });
}

let proteinChart, intakeChart;

// 추천 결과 시각화 함수
function visualizeResult(data) {
    const safeParse = (obj) => {
        if (typeof obj === "string") {
            try {
                return JSON.parse(obj);
            } catch (e) {
                console.warn("JSON 파싱 실패:", obj);
                return {};
            }
        }
        return obj;
    };

    // 건강 상태 바 그래프
    renderHealthConditionBars(safeParse(data.healthConditions));

    // 단백질 유형 차트
    const sortedTypes = Object.entries(safeParse(data.recommendedTypeScores || {})).sort((a, b) => b[1] - a[1]);
    const typeLabels = sortedTypes.map(([k]) => k);
    const typeScores = sortedTypes.map(([_, v]) => v);
    const ctx2 = document.getElementById('protein_type_chart').getContext('2d');
    if (proteinChart) proteinChart.destroy();
    proteinChart = new Chart(ctx2, {
        type: 'pie',
        data: {
            labels: typeLabels,
            datasets: [{
                data: typeScores,
                backgroundColor: ['#F6D8FC', '#E1C8FB', '#C6B8F9', '#BDC1FA', '#DCE2F6', '#8e61cf']
            }]
        },
        options: {
            responsive: false,
            plugins: {
                title: { display: true, text: '추천 단백질 유형' },
                legend: { labels: { font: { size: 12 } } }
            }
        }
    });

    // 섭취 타이밍 차트
    const ctx3 = document.getElementById('protein_intake_chart').getContext('2d');
    if (intakeChart) intakeChart.destroy();
    const timingLabels = Object.keys(safeParse(data.intakeTimingRatio));
    const timingValues = Object.values(safeParse(data.intakeTimingRatio));
    intakeChart = new Chart(ctx3, {
        type: 'doughnut',
        data: {
            labels: timingLabels,
            datasets: [{
                data: timingValues,
                backgroundColor: ['#CFBAF0', '#A3C4F3', '#90DBF4', '#8EECF5', '#98F5E1', '#FFD6A5']
            }]
        },
        options: {
            responsive: false,
            plugins: {
                title: { display: true, text: '섭취 타이밍 비율' },
                legend: {
                    labels: { font: { size: 12 } },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return `${context.label}: ${context.parsed}%`;
                            }
                        }
                    }
                }
            }
        }
    });

    // 기타 텍스트 데이터
    document.getElementById('timing').innerText = `• ${data.intakeTiming}`;
    document.getElementById('intakeAmount').innerText = `• ${data.minIntakeGram}g ~ ${data.maxIntakeGram}g`;
    document.getElementById('recommendedProtein').innerText = `• 추천 단백질: ${data.recommendedTypes.join(', ')}`;
    document.getElementById('avoidProtein').innerText = `• 회피 단백질: ${data.avoidTypes.join(', ')}`;
    document.getElementById('productName').innerText = `추천 제품: ${data.productName}`;

    // 경고 메시지
    const warningsList = document.getElementById('warnings');
    warningsList.innerHTML = '';
    const title = document.createElement('li');
    title.className = 'list-title';
    title.innerText = '[건강 팁]';
    warningsList.appendChild(title);
    (data.warningMessages || []).forEach(msg => {
        const li = document.createElement('li');
        li.innerText = `• ${msg}`;
        warningsList.appendChild(li);
    });
}

// 문서 로딩 완료 후 실행
document.addEventListener("DOMContentLoaded", async () => {
    let surveyResponseId = localStorage.getItem("surveyResponseId");
    const savedData = localStorage.getItem("surveyData");

    // 1. 저장되지 않은 설문 응답 자동 저장
    if (savedData && !surveyResponseId) {
        try {
            const res = await fetch("/api/survey-responses", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: savedData,
            });
            if (res.ok) {
                const resultData = await res.json();
                surveyResponseId = resultData.responseId;
                localStorage.setItem("surveyResponseId", surveyResponseId);
                localStorage.removeItem("surveyData");
            } else {
                alert("설문 저장 실패");
                return;
            }
        } catch (err) {
            console.error("설문 저장 오류:", err);
            return;
        }
    }

    // 2. 세션 복원
    if (!surveyResponseId) {
        try {
            const sessionRes = await fetch("/api/temp-survey", { credentials: "include" });
            if (sessionRes.ok && sessionRes.status !== 204) {
                const sessionData = await sessionRes.json();
                const res = await fetch("/api/survey-responses", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    credentials: "include",
                    body: JSON.stringify(sessionData)
                });
                if (res.ok) {
                    const result = await res.json();
                    surveyResponseId = result.responseId;
                    localStorage.setItem("surveyResponseId", surveyResponseId);
                } else {
                    alert("세션 복원 실패");
                    return;
                }
            }
        } catch (err) {
            console.error("세션 복원 오류:", err);
        }
    }

    // 3. 추천 결과 호출
    if (!surveyResponseId) {
        alert("설문 결과를 불러올 수 없습니다. 다시 설문을 진행해주세요.");
        return;
    }

    try {
        const res = await fetch(`/api/recommendations/${surveyResponseId}`);
        if (res.ok) {
            const resultData = await res.json();
            const userName = resultData.name || localStorage.getItem("userName") || "고객";
            document.getElementById("user_result_title").innerHTML = `${userName}님의 <br /> 프로틴 추천 결과`;
            updateUserInfo({
                gender: resultData.gender,
                age: resultData.age,
                bmi: resultData.bmi,
                bmiStatus: resultData.bmiStatus
            });
            visualizeResult(resultData);
        } else {
            alert("추천 결과를 불러올 수 없습니다.");
        }
    } catch (err) {
        console.error("추천 호출 오류:", err);
        alert("오류가 발생했습니다. 다시 시도해주세요.");
    }

    // 4. 만족도 팝업
    const closeBtn = document.querySelector(".close_btn3");
    const popupSatisfaction = document.querySelector(".popup_bg");
    if (closeBtn && popupSatisfaction) {
        closeBtn.addEventListener("click", () => {
            popupSatisfaction.classList.add("active");
        });
    }

    // 별점 선택
    let selectedRating = 0;
    document.querySelectorAll(".star").forEach(star => {
        star.addEventListener("click", () => {
            selectedRating = parseInt(star.dataset.value);
            document.querySelectorAll(".star").forEach(s => {
                s.classList.remove("filled");
                if (parseInt(s.dataset.value) <= selectedRating) {
                    s.classList.add("filled");
                }
            });
        });
    });

    // 만족도 저장
    document.querySelector('.end_btn').addEventListener('click', async () => {
        const score = selectedRating;
        const responseId = localStorage.getItem("surveyResponseId");
        const surveyId = 1;

        if (!score || !responseId) {
            alert("별점을 선택하거나 응답 ID가 없습니다.");
            return;
        }

        const dto = {
            surveyId: parseInt(surveyId),
            responseId: parseInt(responseId),
            satisfactionScore: score
        };

        try {
            await fetch("/api/satisfaction", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });
            alert("설문이 저장되었습니다!");
            window.location.href = "/";
        } catch (err) {
            alert("저장 중 오류가 발생했습니다.");
            console.error(err);
        }
    });
});