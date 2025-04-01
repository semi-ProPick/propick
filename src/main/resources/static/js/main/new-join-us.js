function validateForm(){
    let user_name = document.getElementById("insert_name").value;
    let user_phone = document.getElementById("insert_phone").value;
    let user_id = document.getElementById("insert_id").value;
    let user_pwd = document.getElementById("insert_pwd").value;
    let user_confirm_pwd = document.getElementById("insert_confirm_pwd").value
    let user_birth = document.getElementById("insert_birth").value;

    let error_name = document.getElementById("error_message1");
    let error_phone = document.getElementById("error_message2");
    let error_id = document.getElementById("error_message3");
    let error_pwd = document.getElementById("error_message4");
    let error_confirm = document.getElementById("error_message5");
    let error_birth = document.getElementById("error_message6");



    let koreanRegex = /^[가-힣]+$/;
    let numberRegex1 = /^[0-9]{10,11}$/;
    let numberRegex = /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/;
    let engNum =  /^[a-zA-Z0-9]*$/;
    let pattern = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~@#$!%*?&])[a-zA-Z\d~@#$!%*?&]{8,}$/;

    let valid = true;

    // 이름 유효성 검사
    if(user_name.trim() === ""){
        error_name.style.display = "block";
        error_name.textContent = "이름을 입려해주세요.";
        valid=false;
    }else if(!koreanRegex.test(user_name)){
        error_name.style.display="block";
        error_name.textContent = "이름을 제대로 입력해주세요.";
        valid=false;
    } else {
        error_name.style.display = "none";
    }

    //연락처 유효성 검사
    if(user_phone.trim() === ""){
        error_phone.style.display = "block";
        error_phone.textContent = "연락처를 입력해주세요.";
        valid=false;
    } else if(!numberRegex1.test(user_phone)){
        error_phone.style.display="block";
        error_phone.textContent = "연락처를 다시 확인해주세요.";
        valid=false;
    } else {
        error_phone.style.display = "none";
    }

    //아이디 유효성 검사
    if(user_id.trim() === ""){
        error_id.style.display ="block";
        error_id.textContent ="ID를 입력해주세요."
        valid=false;
    } else if(!engNum.test(user_id)){
        error_id.style.display="block";
        error_id.textContent="영어와 숫자만 가능합니다.";
        valid=false;
    } else{
        error_id.style.display="none";
    }

    //비밀번호
    if(user_pwd.trim() === ""){
        error_pwd.style.display ="block";
        error_pwd.textContent ="PWD를 입력하세요.";
        valid=false;
    } else if(!pattern.test(user_pwd)){
        error_pwd.style.display="block";
        error_pwd.textContent="영어, 특수문자, 숫자를 포함시키고 8자리 이상으로 입력하세요.";
        valid=false;
    } else{
        error_pwd.style.display="none";
    }

    //비밀번호 확인
    if(user_confirm_pwd.trim() === ""){
        error_confirm.style.display="block";
        error_confirm.textContent="PWD를 한번 더 입력해주세요.";
        valid=false;
    } else if(user_pwd.trim() !== user_confirm_pwd.trim()){
        error_confirm.style.display="blcok";
        error_confirm.textContent = "PWD와 일치하게 입력해주세요.";
        valid=false;
    } else if(!pattern.test(user_confirm_pwd)){
        error_confirm.style.display="block";
        error_confirm.textContent="PWD와 일치하게 입력해주세요.";
        valid=false;
    } else{
        error_confirm.style.display="none";
    }

    //생년월일
    if(user_birth.trim() === ""){
        error_birth.style.display="block";
        error_birth.textContent="생년월일을 입력해주세요.";
        valid=false;
    }else if(!numberRegex.test(user_birth)){
        error_birth.style.display="block";
        error_birth.textContent="00/00/00 양식으로 적어주세요.";
        valid=false;
    } else {
        error_birth.style.display="none";
    }

    if (valid) {
        document.getElementById("joinForm").submit();  // 폼을 실제로 제출
    }
}

function toggleCheckboxes(){
    const select_all = document.getElementById("all_check");
    const checkboxes = document.querySelectorAll(".check");

    checkboxes.forEach(checkbox => {
        checkbox.checked = select_all.checked;
    });
}


document.getElementById('view1').addEventListener('click', function(){
// 첫 번째 전문보기
    const termsContent1 = document.getElementById('terms_content1');
    const isVisible1 = termsContent1.style.display == 'block';

    if(isVisible1){
        termsContent1.style.display = 'none';
        this.textContent= '전문 보기';
    } else {
        termsContent1.style.display = 'block';
        this.textContent = '전문 숨기기';
    }
});



// 두 번째 전문보기
document.getElementById('view2').addEventListener('click', function(){
    const termsContent2 = document.getElementById('terms_content2');
    const isVisible2 = termsContent2.style.display == 'block';

    if(isVisible2){
        termsContent2.style.display = 'none';
        this.textContent= '전문 보기';
    } else {
        termsContent2.style.display = 'block';
        this.textContent = '전문 숨기기';
    }
});

// 세 번째 전문보기
document.getElementById('view3').addEventListener('click', function(){
    const termsContent3 = document.getElementById('terms_content3');
    const isVisible3 = termsContent3.style.display == 'block';

    if(isVisible3){
        termsContent3.style.display = 'none';
        this.textContent= '전문 보기';
    } else {
        termsContent3.style.display = 'block';
        this.textContent = '전문 숨기기';
    }
});