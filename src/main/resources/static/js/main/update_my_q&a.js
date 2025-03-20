
document.addEventListener('DOMContentLoaded', function() {
    const update_btn = document.querySelector('#update_box');
    const popup = document.getElementById('update_popup');
    const confirm_o = document.getElementById('update_confirm_o');
    const confirm_x = document.getElementById('update_confirm_x');
    const success_popup = document.getElementById('success_popup');
    const close_success_popup = document.getElementById('close_success_popup');
    
    // 수정 버튼 클릭 시 팝업 열기
    update_btn.addEventListener('click', function() {
      popup.style.display = 'block'; // 수정 팝업을 보이게 함
    });

    // '확인' 버튼 클릭 시 수정 완료 팝업 열기
    confirm_o.addEventListener('click', function() {
      popup.style.display = 'none';
      successPopup.style.display = 'block';
    });

    // '취소' 버튼 클릭 시 수정 팝업 닫기
    confirm_x.addEventListener('click', function() {
      popup.style.display = 'none';
    });

    // '확인' 버튼 클릭 시 수정 완료 팝업 닫기
    close_success_popup.addEventListener('click', function() {
      success_popup.style.display = 'none';
    });
  });




document.addEventListener('DOMContentLoaded', function() {
    const delete_btn = document.querySelector('#delete_box');
    const popup = document.getElementById('delete_popup');
    const confirm_o = document.getElementById('delete_confirm_o');
    const confirm_x = document.getElementById('delete_confirm_x');
    const success_popup = document.getElementById('success_popup');
    const close_success_popup = document.getElementById('close_success_popup');
    
    // 수정 버튼 클릭 시 팝업 열기
    delete_btn.addEventListener('click', function() {
      popup.style.display = 'block'; // 수정 팝업을 보이게 함
    });

    // '확인' 버튼 클릭 시 수정 완료 팝업 열기
    confirm_o.addEventListener('click', function() {
      popup.style.display = 'none';
      successPopup.style.display = 'block';
    });

    // '취소' 버튼 클릭 시 수정 팝업 닫기
    confirm_x.addEventListener('click', function() {
      popup.style.display = 'none';
    });

    // '확인' 버튼 클릭 시 수정 완료 팝업 닫기
    close_success_popup.addEventListener('click', function() {
      success_popup.style.display = 'none';
    });
  });