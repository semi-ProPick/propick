document.addEventListener("DOMContentLoaded", () => {
    const surveyResponseId = localStorage.getItem("surveyResponseId");
    const userName = localStorage.getItem("userName") || "고객";
    const closeBtn = document.querySelector(".close_btn3");
    const popupSatisfaction = document.querySelector(".popup_bg");

    document.querySelector("#user_result_title").innerHTML = `${userName}님의 <br /> 프로틴 추천 결과`;

    if (closeBtn && popupSatisfaction) {
        closeBtn.addEventListener("click", () => {
            popupSatisfaction.classList.add("active");
        });
    }

    if (!surveyResponseId) {
        alert("설문 결과를 불러올 수 없습니다.");
        return;
    }

    fetch(`/api/recommendations/${surveyResponseId}`)
        .then(res => res.json())
        .then(data => {
            updateUserInfo(data);
            visualizeResult(data);

            // ✅ 추천 제품 텍스트 출력
            document.getElementById('productName').innerText = `추천 제품: ${data.productName}`;
        })
        .catch(err => console.error("결과 불러오기 실패:", err));

    // ✅ 별점 클릭 이벤트 추가
    document.querySelectorAll('.star').forEach(star => {
        star.addEventListener('click', () => {
            const rating = star.getAttribute('data-value');
            console.log(`⭐ ${rating}점 선택됨`);
            // TODO: 서버에 전송하거나 로컬에 저장하기
        });
    });
});

// fetch(`/api/recommendations/${surveyResponseId}`)
    //     .then(res => res.json())
    //     .then(data => {
    //         updateUserInfo(data);
    //         visualizeResult(data);
    //     })
    //     .catch(err => console.error("결과 불러오기 실패:", err));

// 사용자 정보 박스 업데이트 함수
function updateUserInfo(data) {
    const userText = `성별: ${data.gender} | 나이: ${data.age}세 | BMI: ${data.bmi} (${data.bmiStatus})`;
    document.getElementById("user_info_text").innerText = userText;
}

// 차트 객체를 전역 변수로 선언하여 중복 생성을 방지
let healthChart, proteinChart, intakeChart;

// 추천 결과 시각화 함수
function visualizeResult(data) {
    console.log("결과 데이터:", data);

    // [1] 기본 정보 시각화
    // document.getElementById('bmi').innerText = `BMI: ${data.bmi} (${data.bmiStatus})`;
    // document.getElementById('gender').innerText = `성별: ${data.gender}`;
    // document.getElementById('age').innerText = `나이: ${data.age}`;

    // -->html 반영
    document.getElementById('user_info_text').innerText =
        `성별: ${data.gender} | 나이: ${data.age}세 | BMI: ${data.bmi} (${data.bmiStatus})`;

// 결과 시각화
    // ✅ 중복 제거된 추천 결과 시각화 함수
    function visualizeResult(data) {
        console.log("결과 데이터:", data);

        // 1. 사용자 정보
        document.getElementById('user_info_text').innerText =
            `성별: ${data.gender} | 나이: ${data.age}세 | BMI: ${data.bmi} (${data.bmiStatus})`;

        // 2. 건강 상태 차트
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
                    title: {
                        display: true,
                        text: '건강 상태 유형'
                    },
                    legend: {
                        labels: {
                            font: {
                                size: 12
                            }
                        }
                    }
                },
                scale: {
                    ticks: {
                        min: 0,
                        max: 100,
                        stepSize: 20
                    }
                }
            }
        });

        // 3. 단백질 유형 차트
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
                    title: {
                        display: true,
                        text: '추천 단백질 유형'
                    },
                    legend: {
                        labels: {
                            font: {
                                size: 12
                            }
                        }
                    }
                }
            }
        });

        // 4. 섭취 타이밍 차트
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
                    title: {
                        display: true,
                        text: '섭취 타이밍 비율'
                    },
                    legend: {
                        labels: {
                            font: {
                                size: 12
                            }
                        },
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

        // 5. 텍스트 정보
        document.getElementById('timing').innerText = `• ${data.intakeTiming}`;
        document.getElementById('intakeAmount').innerText = `• ${data.minIntakeGram}g ~ ${data.maxIntakeGram}g`;
        document.getElementById('recommendedProtein').innerText = `• 추천 단백질: ${data.recommendedTypes.join(', ')}`;
        document.getElementById('avoidProtein').innerText = `• 회피 단백질: ${data.avoidTypes.join(', ')}`;
        document.getElementById('productName').innerText = `추천 제품: ${data.productName}`;

        // 6. 건강 팁 메시지
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


    // [8] 추천 제품명 (DTO: productName)
    document.getElementById('productName').innerText = `추천 제품: ${data.productName}`;
}

