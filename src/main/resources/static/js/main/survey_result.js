fetch(`/api/recommendations/${surveyResponseId}`)
    .then(response => response.json())
    .then(data => {
        visualizeResult(data);  // 시각화 함수 호출
    })
    .catch(error => console.error('에러:', error));
function visualizeResult(data) {
    // 데이터 확인
    console.log(data);

    // BMI 시각화 (텍스트로)
    document.getElementById('bmi').innerText = `BMI: ${data.bmi} (${data.bmiStatus})`;
    // 성별 시각화 (텍스트로)
    document.getElementById('gender').innerText = `성별: ${data.gender}`;
    // 나이 시각화 (텍스트로)
    document.getElementById('age').innerText = `나이: ${data.age}`;

    // 건강 상태 차트 (레이더 차트)
    const ctx1 = document.getElementById('health_conditions_chart').getContext('2d');
    new Chart(ctx1, {
        type: 'radar',
        data: {
            labels: analysisData.healthConditions.labels,
            datasets: [{
                label: '건강 상태 가중치',
                data: analysisData.healthConditions.scores,
                backgroundColor: ['#F5EDEB'],
                borderColor: '#9b72cf',
                pointBackgroundColor: '#9b72cf',
                pointBorderColor: '#fff'
            }]
        },
        options: { scale: { min: 0, max: 100 } }
    });


// 추천 단백질 유형 차트 (파이 차트)
    const ctx2 = document.getElementById('protein_type_chart').getContext('2d');
    new Chart(ctx2, {
        type: 'pie',
        data: {
            labels: Object.keys(analysisData.recommendedTypes),
            datasets: [{
                data: Object.values(analysisData.recommendedTypes),
                backgroundColor: ['#F5EDEB']
            }]
        }
    });

// 섭취 타이밍 차트 (도넛 차트)
    const ctx3 = document.getElementById('protein_intake_chart').getContext('2d');
    new Chart(ctx3, {
        type: 'doughnut',
        data: {
            labels: Object.keys(analysisData.intakeTiming),
            datasets: [{
                data: Object.values(analysisData.intakeTiming),
                backgroundColor: ['#F5EDEB']
            }]
        }
    });


    // 추천 단백질 타입 (텍스트로)
    document.getElementById('recommendedProtein').innerText = `추천 단백질: ${data.recommendedTypes.join(", ")}`;

    // 피해야 할 단백질 (텍스트로)
    document.getElementById('avoidProtein').innerText = `회피 단백질: ${data.avoidTypes.join(", ")}`;

    // 프로틴 섭취량 (텍스트로)
    document.getElementById('maxIntakeGram').innerText ='섭취 단백질: ${data.maxIntakeGram}`;

    // 추천 섭취 타이밍 (텍스트로)
    document.getElementById('timing').innerText = `권장 섭취 시간: ${data.intakeTiming}`;

    // 건강 경고 메시지 (리스트 형태로)
    const warningsList = document.getElementById('warnings');
    data.warningMessages.forEach(msg => {
        const li = document.createElement('li');
        li.innerText = msg;
        warningsList.appendChild(li);
    });
}
