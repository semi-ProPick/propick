console.log("result.js loaded. current page:", window.location.pathname);

// 유저 정보 출력
function updateUserInfo({ gender, age, bmi, bmiStatus }) {
    const infoText = document.getElementById("user_info_text");
    const genderText = gender === "FEMALE" ? "여성" : gender === "MALE" ? "남성" : "기타";
    if (infoText) {
        infoText.innerText = `성별: ${genderText}   |   나이: ${age}세   |   BMI: ${bmi} (${bmiStatus})`;
    }
}

let proteinChart, intakeChart;

// 북마크 이벤트 등록 함수
function attachBookmarkEvents() {
    document.querySelectorAll(".bookmark-icon").forEach(button => {
        button.removeEventListener("click", button._bookmarkHandler); // 중복 제거
        button._bookmarkHandler = async function (e) {
            e.preventDefault();
            const productId = this.dataset.productId;
            const isBookmarked = this.classList.contains("bookmarked");

            try {
                let res;
                if (isBookmarked) {
                    res = await fetch(`/bookmark/remove/${productId}`, { method: "DELETE" });
                } else {
                    res = await fetch(`/bookmark/add`, {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded" },
                        body: new URLSearchParams({ productId })
                    });
                }

                const result = await res.json();
                if (result.success) {
                    this.classList.toggle("bookmarked");
                } else {
                    alert(result.error || "처리 중 오류가 발생했습니다.");
                }
            } catch (e) {
                console.error("북마크 오류:", e);
                alert("서버 통신 중 오류 발생");
            }
        };
        button.addEventListener("click", button._bookmarkHandler);
    });
}

// 추천 결과 시각화
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

    // 건강상태 점수 바 차트
    const fixedCategories = ["소화 장", "피부 질환", "신장 부담", "수면 장애", "관절 건강", "간 건강", "혈관 건강"];
    const healthData = safeParse(data.healthConditions);
    console.log("건강상태점수:", healthData);
    const labels = fixedCategories;
    const dataValues = fixedCategories.map(k => healthData[k] ?? 0); // 없으면 0으로 처리

    const ctx1 = document.getElementById("health_conditions_chart").getContext("2d");
    if (window.childChart) window.childChart.destroy();
    window.childChart = new Chart(ctx1, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "하위 증상별 점수",
                data:  dataValues,
                backgroundColor: "rgba(255, 159, 64, 0.4)",
                borderColor: "#FF9F40",
                borderWidth: 2
            }]
        },
        options: {
            responsive: false,
            maintainAspectRatio: false,
            indexAxis: "y",
            plugins: {
                title: { display: true, text: "건강상태 점수 분석", font: { size: 20 } },
                legend: { display: false }
            },
            scales: {
                x: { min: 0, max: 100, ticks: { stepSize: 20 } },
                y: { ticks: { font: { size: 12 } } }
            }
        }
    });

    // 단백질 유형 차트
    const sortedTypes = Object.entries(safeParse(data.recommendedTypeScores || {})).sort((a, b) => b[1] - a[1]);
    const ctx2 = document.getElementById("protein_type_chart").getContext("2d");
    if (proteinChart) proteinChart.destroy();
    proteinChart = new Chart(ctx2, {
        type: "pie",
        data: {
            labels: sortedTypes.map(([k]) => k),
            datasets: [{ data: sortedTypes.map(([_, v]) => v), backgroundColor: ['#F6D8FC', '#E1C8FB', '#C6B8F9', '#BDC1FA', '#DCE2F6', '#8e61cf'] }]
        },
        options: {
            responsive: false,
            plugins: {
                title: { display: true, text: "추천 단백질 유형", font: { size: 20 } },
                legend: { labels: { font: { size: 12 } } }
            }
        }
    });

    // 섭취 타이밍 차트
    const timingData = safeParse(data.intakeTimingRatio);
    const ctx3 = document.getElementById("protein_intake_chart").getContext("2d");
    if (intakeChart) intakeChart.destroy();
    intakeChart = new Chart(ctx3, {
        type: "doughnut",
        data: {
            labels: Object.keys(timingData),
            datasets: [{ data: Object.values(timingData), backgroundColor: ['#CFBAF0', '#A3C4F3', '#90DBF4', '#8EECF5', '#98F5E1', '#FFD6A5'] }]
        },
        options: {
            responsive: false,
            plugins: {
                title: { display: true, text: "섭취 타이밍 비율", font: { size: 20 } },
                legend: { labels: { font: { size: 12 } } }
            }
        }
    });

    // 기타 텍스트
    document.getElementById("timing").innerText = `• ${data.intakeTiming}`;
    document.getElementById("intakeAmount").innerText = `• ${data.minIntakeGram}g ~ ${data.maxIntakeGram}g`;
    document.getElementById("recommendedProtein").innerText = `• 추천 단백질: ${data.recommendedTypes.join(", ")}`;
    document.getElementById("avoidProtein").innerText = `• 회피 단백질: ${data.avoidTypes.join(", ")}`;

    // 경고 메시지
    const warningsList = document.getElementById("warnings");
    warningsList.innerHTML = '<li class="list-title">[건강 팁]</li>';
    (data.warningMessages || []).forEach(msg => {
        const li = document.createElement("li");
        li.innerText = `• ${msg}`;
        warningsList.appendChild(li);
    });
}

// 추천 상품 카드 렌더링
function renderRecommendedProductCards(productList) {
    const container = document.getElementById("recommendedProductList");
    if (!container) return;

    container.innerHTML = "";

    productList.forEach(product => {
        const li = document.createElement("li");
        li.className = "product-item";

        li.innerHTML = `
            <a href="/products/${product.productId}">
                <div class="product-images">
                    <img src="${product.productImages?.[0] || '/images/no-image.png'}" alt="${product.productName}" />
                    <p>${product.productName}</p>
                </div>
                <div class="product-price">
                    ${product.discountRate > 0
            ? `<span style="text-decoration: line-through;">${product.productPrice.toLocaleString()}원</span>
                           <span style="color:red; font-weight:bold;"> ${product.discountedPrice.toLocaleString()}원</span>
                           <span style="font-size: 15px;">(${product.discountRate}% 할인)</span>`
            : `<span>${product.productPrice.toLocaleString()}원</span>`}
                </div>
            </a>
            <button class="bookmark-icon ${product.bookmarked ? "bookmarked" : ""}" data-product-id="${product.productId}">
                <i class="fas fa-bookmark"></i>
            </button>
        `;
        container.appendChild(li);
    });

    attachBookmarkEvents(); // 🔁 북마크 이벤트 적용
}

// 메인 실행 로직
document.addEventListener("DOMContentLoaded", async () => {
    let surveyResponseId = localStorage.getItem("surveyResponseId");
    const savedData = localStorage.getItem("surveyData");

    // 1. 저장되지 않은 설문 자동 저장
    if (savedData && !surveyResponseId) {
        try {
            const res = await fetch("/api/survey-responses", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: savedData
            });
            if (res.ok) {
                const result = await res.json();
                surveyResponseId = result.responseId;
                localStorage.setItem("surveyResponseId", surveyResponseId);
                localStorage.removeItem("surveyData");
            } else {
                alert("설문 저장 실패");
                return;
            }
        } catch (e) {
            console.error("설문 저장 오류:", e);
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
                }
            }
        } catch (e) {
            console.error("세션 복원 오류:", e);
        }
    }

    // 3. 추천 결과 불러오기
    if (!surveyResponseId) {
        alert("설문 결과를 불러올 수 없습니다.");
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
            renderRecommendedProductCards(resultData.matchedProducts);
        } else {
            alert("추천 결과를 불러올 수 없습니다.");
        }
    } catch (err) {
        console.error("추천 호출 오류:", err);
        alert("오류가 발생했습니다.");
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
                s.classList.toggle("filled", parseInt(s.dataset.value) <= selectedRating);
            });
        });
    });

    // 만족도 저장
    document.querySelector(".end_btn").addEventListener("click", async () => {
        const responseId = localStorage.getItem("surveyResponseId");
        const dto = {
            surveyId: 1,
            responseId: parseInt(responseId),
            satisfactionScore: selectedRating
        };

        if (!selectedRating || !responseId) {
            alert("별점 또는 설문 정보가 부족합니다.");
            return;
        }

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
