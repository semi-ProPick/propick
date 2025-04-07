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

    // API로 설문 목록 불러오기
    fetch("/api/survey-responses/my_survey")
        .then(res => res.json())
        .then(data => {
            if (data.length === 0) {
                popupEmpty.classList.add("active");
                return;
            }

            data.forEach(item => {
                const li = document.createElement("li");
                li.className = "survey_item";
                li.innerHTML = `
                    <label>
                        <input type="checkbox" class="survey_checkbox" data-id="${item.responseId}" />
                        <span class="survey_title clickable" data-id="${item.responseId}">
                            ${item.surveyTitle}
                        </span>
                    </label>
                    <span class="date">${item.responseDate}</span>
                `;
                surveyList.appendChild(li);
            });

            addSurveyEventListeners();
        })
        .catch(err => {
            console.error("설문 목록 로딩 실패:", err);
        });

    function addSurveyEventListeners() {
        selectAll.addEventListener("change", () => {
            document.querySelectorAll(".survey_checkbox").forEach(cb => {
                cb.checked = selectAll.checked;
            });
        });

        document.querySelectorAll(".survey_title").forEach(el => {
            el.addEventListener("click", e => {
                const id = e.target.dataset.id;
                localStorage.setItem("surveyResponseId", id);
                window.location.href = "/survey_result.html";
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
