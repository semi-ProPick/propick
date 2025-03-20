document.addEventListener("DOMContentLoaded", function () {
    let questions = document.querySelectorAll(".td-btn");

    questions.forEach(button => {
        button.addEventListener("click", function () {
            let targetId = this.getAttribute("data-target");
            let answer = document.getElementById(targetId);

            if (answer.style.display === "none" || answer.style.display === "") {
                answer.style.display = "block";
            } else {
                answer.style.display = "none";
            }
        });
    });
});