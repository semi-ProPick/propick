document.addEventListener('DOMContentLoaded', () => {
    const submitButton = document.querySelector('.button-container button'); // 첫 번째 '문의하기' 버튼
    const titleBox = document.querySelector('.title-box');
    const contentBox = document.querySelector('.content-box');
    const uploadModal = document.getElementById('uploadModal');
    const cancelButton = document.querySelector('.cancel-btn');
    const cancelModal = document.getElementById('cancelModal');
    const yesButton = document.querySelector('.yes-btn');
    const noButton = document.querySelector('.no-btn');

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
    cancelButton.addEventListener('click', (event) => {
        event.preventDefault();
        cancelModal.style.display = 'block';
    });



cancelButton.addEventListener('click', () => {
    cancelModal.style.display = 'block';
});

    // "예" 버튼 클릭 시 모달 숨김 + 추가적인 취소 동작 가능
    yesButton.addEventListener('click', () => {
        cancelModal.style.display = 'none';
        // 예 버튼 클릭 시 추가 로직이 필요하면 여기에 추가
        window.location.href = "/main/q&a_board";
    });

    // "취소" 버튼 클릭 시 모달 숨김
    noButton.addEventListener('click', () => {
        cancelModal.style.display = 'none';
        overlay.style.display = "none"; // 배경 숨김
    });
});
