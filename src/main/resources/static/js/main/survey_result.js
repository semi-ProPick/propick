console.log("✅ result.js loaded. current page:", window.location.pathname);
function updateUserInfo({ gender, age, bmi, bmiStatus }) {
    const infoText = document.getElementById("user_info_text");
    const genderText = gender === "FEMALE" ? "여성" : gender === "MALE" ? "남성" : "기타";

    if (infoText) {
        infoText.innerText = `성별: ${genderText}   |   나이: ${age}세   |   BMI: ${bmi} (${bmiStatus})`;
    }
}



document.addEventListener("DOMContentLoaded", async () => {
    let surveyResponseId = localStorage.getItem("surveyResponseId");
    console.log(" surveyResponseId:", surveyResponseId);
    const savedData = localStorage.getItem("surveyData");
    const parsedData = savedData ? JSON.parse(savedData) : null;

    // ✅ 1. 로컬스토리지 + 로그인 완료 후 → 자동 전송
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
                console.log("추천 결과:", resultData);
                console.log("healthConditions:", resultData.healthConditions);
                console.log("recommendedTypeScores:", resultData.recommendedTypeScores);
                console.log("intakeTimingRatio:", resultData.intakeTimingRatio);
                surveyResponseId = resultData.responseId;
                localStorage.setItem("surveyResponseId", surveyResponseId);
                localStorage.removeItem("surveyData");
                console.log("설문 저장 완료:", surveyResponseId);
            } else {
                alert("설문 저장 실패");
                return;
            }
        } catch (err) {
            console.error("설문 저장 중 오류:", err);
            return;
        }
    }
    // ✅ 2. 백엔드 세션 기반 임시 데이터 복원 시도
    // ✅ 세션 복원 시도
    if (!surveyResponseId) {
        try {
            const sessionRes = await fetch("/api/temp-survey", {
                credentials: "include"
            });

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
                    console.log("세션에서 설문 복원 후 저장 완료:", surveyResponseId);
                } else {
                    alert("세션 복원 설문 저장 실패");
                    return;
                }
            } else {
                console.log("세션에서 복원할 설문 데이터 없음");
            }
        } catch (err) {
            console.error("세션 설문 복원 중 오류:", err);
        }
    }

// ✅ 추천 호출 전에 필수 체크
    if (!surveyResponseId) {
        alert("설문 결과를 불러올 수 없습니다. 다시 설문을 진행해주세요.");
        return;
    }
    // ✅ 3. 추천 결과 데이터 불러오기 및 화면 렌더링
    try {
        const res = await fetch(`/api/recommendations/${surveyResponseId}`);
        if (res.ok) {
            const resultData = await res.json();

            const userName = resultData.name || localStorage.getItem("userName") || "고객";
            document.getElementById("user_result_title").innerHTML = `${userName}님의 <br /> 프로틴 추천 결과`;

            // 사용자 정보 업데이트
            updateUserInfo({
                gender: resultData.gender,
                age: resultData.age,
                bmi: resultData.bmi,
                bmiStatus: resultData.bmiStatus
            });

            // 결과 시각화
            visualizeResult(resultData);
        } else {
            alert("추천 결과를 불러오는 데 실패했습니다.");
        }
    } catch (err) {
        console.error("추천 결과 호출 오류:", err);
        alert("오류가 발생했습니다. 다시 시도해주세요.");
    }



    // [4] 만족도 팝업 처리
    const closeBtn = document.querySelector(".close_btn3");
    const popupSatisfaction = document.querySelector(".popup_bg");
    if (closeBtn && popupSatisfaction) {
        closeBtn.addEventListener("click", () => {
            popupSatisfaction.classList.add("active");
        });
    }

    // [5] 별점 클릭 이벤트
    let selectedRating = 0;

    document.querySelectorAll(".star").forEach(star => {
        star.addEventListener("click", () => {
            selectedRating = parseInt(star.dataset.value);

            // ⭐ 기존 selected → filled로 클래스 변경
            document.querySelectorAll(".star").forEach(s => {
                s.classList.remove("filled");
                if (parseInt(s.dataset.value) <= selectedRating) {
                    s.classList.add("filled");
                }
            });

            console.log("⭐ 선택된 별점:", selectedRating);
        });
    });
    // [6] 만족도 제출
    document.querySelector('.end_btn').addEventListener('click', async () => {
        const score = selectedRating;
        const responseId = localStorage.getItem("surveyResponseId");
        const surveyId = 1;

        if (!score || !responseId) {
            alert("만족도를 선택하거나 응답 ID가 없습니다.");
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



// 전역 차트 객체
let healthChart, proteinChart, intakeChart;

// 결과 시각화 함수
function visualizeResult(data) {
    // ✨ JSON 형태일 경우를 대비해 파싱
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

    const labels = ["소화 장", "피부 질환", "신장 부담", "수면 장애", "관절 건강", "간 건강", "혈관 건강"];
    const parsed = safeParse(data.healthConditions);

// 전체 데이터 그대로
    const fullData = labels.map(label => parsed[label] || 0);

// 상위 3개만 추출해서 연결할 값 구성
    const sorted = labels.map(label => ({ label, value: parsed[label] || 0 }))
        .sort((a, b) => b.value - a.value)
        .slice(0, 3);

// 상위 3개 연결용 데이터: 나머지는 0
    const top3Data = labels.map(label => {
        return sorted.find(item => item.label === label) ? parsed[label] : 0;
    });

    const ctx1 = document.getElementById('health_conditions_chart').getContext('2d');
    if (healthChart) healthChart.destroy();

    healthChart = new Chart(ctx1, {
        type: 'radar',
        data: {
            labels,
            datasets: [
                {
                    label: '건강 상태 점수',
                    data: fullData,
                    backgroundColor: 'rgba(155, 114, 207, 0.7)',
                    borderColor: '#C6B8F9',
                    pointBackgroundColor: '#231A3F'
                },
                {
                    label: '강조된 상위 3개',
                    data: top3Data,
                    backgroundColor: 'rgba(155, 114, 207, 0.5)', // 기존색 유지
                    borderColor: '#231A3F',
                    pointBackgroundColor: '#231A3F',
                    borderWidth: 2,
                    fill: true
                }
            ]
        },
        options: {
            responsive: false,
            plugins: {
                title: { display: true, text: '건강 상태 유형' },
                legend: { display: false }  // 원하면 보여줘도 돼
            },
            scales: {
                r: {
                    min: 0,
                    max: 100,
                    ticks: { stepSize: 30, display: false }
                }
            }
        }
    });

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

    // 텍스트 데이터 렌더링
    document.getElementById('timing').innerText = `• ${data.intakeTiming}`;
    document.getElementById('intakeAmount').innerText = `• ${data.minIntakeGram}g ~ ${data.maxIntakeGram}g`;
    document.getElementById('recommendedProtein').innerText = `• 추천 단백질: ${data.recommendedTypes.join(', ')}`;
    document.getElementById('avoidProtein').innerText = `• 회피 단백질: ${data.avoidTypes.join(', ')}`;
    document.getElementById('productName').innerText = `추천 제품: ${data.productName}`;

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
