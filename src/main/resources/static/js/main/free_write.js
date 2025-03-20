document.addEventListener('DOMContentLoaded', () => {
    const submitButton = document.querySelector('.button-container button'); // 첫 번째 '문의하기' 버튼
    const titleBox = document.querySelector('.title-box');
    const contentBox = document.querySelector('.content-box');
    const uploadModal = document.getElementById('uploadModal');

    submitButton.addEventListener('click', () => {
        const title = titleBox.value.trim();
        const content = contentBox.value.trim();
    
        if (title === '' || content === '') {
            alert('제목과 내용을 모두 입력해 주세요.');
        } else {
            uploadModal.style.display = 'block';
    
            // 2초 후 모달 숨기기
            setTimeout(() => {
                uploadModal.style.display = 'none';
            }, 2000);
        }
    });

    // 취소 버튼 관련 (선택사항: 이미 있으시면 무시!)
    const cancelButton = document.querySelector('.cancel-btn');
    const cancelModal = document.getElementById('cancelModal');

    cancelButton.addEventListener('click', () => {
        cancelModal.style.display = 'block';
    });
});


const cancelButton = document.querySelector('.cancel-btn');
const cancelModal = document.getElementById('cancelModal');
const yesButton = document.querySelector('.yes-btn');
const noButton = document.querySelector('.no-btn');

cancelButton.addEventListener('click', () => {
    cancelModal.style.display = 'block';
});

// "예" 버튼 클릭 시 모달 숨김
yesButton.addEventListener('click', () => {
    cancelModal.style.display = 'none';
    // 여기에 추가적으로 취소 동작 넣을 수 있음 (필요시)
});

// "취소" 버튼 클릭 시 모달 숨김
noButton.addEventListener('click', () => {
    cancelModal.style.display = 'none';
});