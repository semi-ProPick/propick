function addComment() {
    var commentBox = document.getElementById("comment-box");
    var commentText = commentBox.value.trim();
    
    if (commentText !== "") {
        var commentSection = document.getElementById("comment-section");
        var newComment = document.createElement("div");
        newComment.classList.add("comment-item");
        newComment.innerHTML = `<strong>익명:</strong> ${commentText}`;
        commentSection.appendChild(newComment);
        commentBox.value = "";
    } else {
        alert("댓글을 입력하세요.");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const likeButton = document.getElementById("like-button");

    likeButton.addEventListener("click", function () {

        if (likeButton.classList.contains("liked")) {
            likeButton.classList.remove("liked");
            likeButton.textContent = "👍";
        } else {
            likeButton.classList.add("liked");
            likeButton.textContent = "❤️";
        }
    });
});