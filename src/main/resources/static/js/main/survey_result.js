console.log("✅ result.js loaded. current page:", window.location.pathname);


document.addEventListener("DOMContentLoaded", async () => {
    let surveyResponseId = localStorage.getItem("surveyResponseId");
    const savedData = localStorage.getItem("surveyData");

    // [1] 설문 응답 저장
    if (savedData && !surveyResponseId) {
        try {
            const res = await fetch("/api/survey-responses", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: savedData,
            });

            if (res.ok) {
                const result = await res.json();
                surveyResponseId = result.responseId;
                localStorage.setItem("surveyResponseId", surveyResponseId);
                localStorage.removeItem("surveyData");
                console.log("✅ 설문 저장 완료:", surveyResponseId);
            } else {
                alert("설문 저장 실패");
                return;
            }
        } catch (err) {
            console.error("설문 저장 중 오류:", err);
            return;
        }
    }

    // [2] 설문 응답 ID가 없으면 종료
    if (!surveyResponseId) {
        alert("설문 결과를 불러올 수 없습니다.");
        return;
    }

    // [3] 추천 결과 불러오기
    try {
        const res = await fetch(`/api/recommendations/${surveyResponseId}`, {
            credentials: "include",
        });

        if (!res.ok) throw new Error(`추천 결과 API 실패 (status: ${res.status})`);

        const data = await res.json();
        const userName = localStorage.getItem("userName") || "고객";
        document.querySelector("#user_result_title").innerHTML = `${userName}님의 <br /> 프로틴 추천 결과`;

        updateUserInfo(data);
        visualizeResult(data);
        document.getElementById('productName').innerText = `추천 제품: ${data.productName}`;
    } catch (err) {
        alert("설문 결과를 불러올 수 없습니다.");
        console.error("결과 불러오기 실패:", err);
        return;
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
            document.querySelectorAll(".star").forEach(s => {
                s.classList.remove("selected");
                if (parseInt(s.dataset.value) <= selectedRating) {
                    s.classList.add("selected");
                }
            });
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
            window.location.href = "/store";
        } catch (err) {
            alert("저장 중 오류가 발생했습니다.");
            console.error(err);
        }
    });
});


// 사용자 정보 표시
function updateUserInfo(data) {
    const userText = `성별: ${data.gender} | 나이: ${data.age}세 | BMI: ${data.bmi} (${data.bmiStatus})`;
    document.getElementById("user_info_text").innerText = userText;
}

// 전역 차트 객체
let healthChart, proteinChart, intakeChart;

// 결과 시각화 함수
function visualizeResult(data) {
    // 건강 상태 차트
    const healthLabels = Object.keys(data.healthConditions);
    const healthScores = Object.values(data.healthConditions);
    const ctx1 = document.getElementById('health_conditions_chart').getContext('2d');
    if (healthChart) healthChart.destroy();
    healthChart = new Chart(ctx1, {
        type: 'radar',
        data: {
            labels: healthLabels,
            datasets: [{
                label: '건강 상태 점수',
                data: healthScores,
                backgroundColor: 'rgba(155, 114, 207, 0.7)',
                borderColor: '#C6B8F9',
                pointBackgroundColor: '#231A3F'
            }]
        },
        options: {
            responsive: false,
            plugins: {
                title: { display: true, text: '건강 상태 유형' },
                legend: { labels: { font: { size: 12 } } }
            },
            scale: {
                ticks: { min: 0, max: 100, stepSize: 20 }
            }
        }
    });

    // 단백질 유형 차트
    const sortedTypes = Object.entries(data.recommendedTypeScores || {}).sort((a, b) => b[1] - a[1]);
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
    const timingLabels = Object.keys(data.intakeTimingRatio || {});
    const timingValues = Object.values(data.intakeTimingRatio || {});
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
