document.addEventListener('DOMContentLoaded', () => {
    const submitButton = document.querySelector('.button-container button');
    const commentBox = document.querySelector('.comment-box');
    const uploadModal = document.getElementById('uploadModal');

    submitButton.addEventListener('click', () => {
        const comment = commentBox.value.trim();
    
        if (comment === '') {
            alert('내용을 입력해 주세요.');
        } else {
            uploadModal.style.display = 'flex';
    
            // 2초 후 모달 숨기기
            setTimeout(() => {
                uploadModal.style.display = 'none';
            }, 2000);
        }
    });
});