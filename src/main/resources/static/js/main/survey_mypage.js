document.addEventListener("DOMContentLoaded", () => {
    const surveyList = document.querySelector(".survey_list");
    const popupDelete = document.querySelector(".popup_bg.confirm_delete");
    const popupEmpty = document.querySelector(".popup_bg.empty_result");

    const selectAll = document.querySelector(".select_all");
    const deleteBtn = document.querySelector(".delete_btn");
    const keepBtn = document.querySelector(".keep_btn");
    const deleteConfirmBtn = document.querySelector(".end_btn");

    // ✅ 추천받기 버튼 클릭 이벤트는 여기서 등록!
    const recommendBtn = document.querySelector(".recommend_btn");
    if (recommendBtn) {
        recommendBtn.addEventListener("click", () => {
            window.location.href = "/survey_start.html";
        });
    }
// ✅ API 호출
    fetch("/api/survey-responses/my_survey")
        .then(res => {
            if (!res.ok) {
                if (res.status === 401) {
                    alert("로그인이 필요합니다.");
                    window.location.href = "/user/login?redirect=/survey_mypage";
                    return;
                }
                throw new Error(`HTTP 오류: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            if (!data || data.length === 0) {
                popupEmpty.classList.add("active");
                return;
            }
            renderSurveyList(data);
        })
        .catch(err => {
            console.error("설문 목록 로딩 실패:", err);
        });

    // ✅ 리스트 렌더링 함수
    function renderSurveyList(data) {
        data.forEach(item => {
            const li = document.createElement("li");
            li.className = "survey_item";
            li.innerHTML = `
                <label>
                    <input type="checkbox" class="survey_checkbox" data-id="${item.responseId}" />
                    <span class="survey_title clickable" data-id="${item.responseId}">
                        ${item.surveyTitle} (${item.purpose || ''}${item.mainConcerns?.length ? ', ' + item.mainConcerns.join(', ') : ''})
                    </span>
                </label>
                <span class="date">${item.responseDate}</span>
            `;
            surveyList.appendChild(li);
        });

        addSurveyEventListeners(); // ✅ 렌더링 후 이벤트 등록
    }

    // ✅ 이벤트 등록 함수
    function addSurveyEventListeners() {
        selectAll.addEventListener("change", () => {
            document.querySelectorAll(".survey_checkbox").forEach(cb => {
                cb.checked = selectAll.checked;
            });
        });

        document.querySelectorAll(".survey_title").forEach(el => {
            el.addEventListener("click", e => {
                const id = e.target.dataset.id;
                localStorage.setItem("surveyResponseId", id);  // 저장
                window.location.href = "/survey_result";  // 결과 페이지 이동
            });
        });


        deleteBtn.addEventListener("click", () => {
            const selected = document.querySelectorAll(".survey_checkbox:checked");
            if (selected.length === 0) {
                alert("삭제할 설문을 선택하세요!");
                return;
            }
            popupDelete.classList.add("active");
        });

        keepBtn.addEventListener("click", () => {
            popupDelete.classList.remove("active");
        });

        deleteConfirmBtn.addEventListener("click", () => {
            const selected = document.querySelectorAll(".survey_checkbox:checked");

            const deletePromises = Array.from(selected).map(cb => {
                const id = cb.dataset.id;
                return fetch(`/api/survey-responses/my_survey/${id}`, {
                    method: "DELETE"
                });
            });

            Promise.all(deletePromises)
                .then(() => {
                    console.log("삭제 완료 후 새로고침");
                    location.reload();
                })
                .catch(err => {
                    console.error("삭제 중 에러:", err);
                });
        });
    }
});