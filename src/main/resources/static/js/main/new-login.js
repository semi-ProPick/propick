function validateForm(){
    let user_id = document.getElementById("insert_id").value
    let user_pwd = document.getElementById("insert_pwd").value

    let error_id = document.getElementById("error_message1");
    let error_pwd = document.getElementById("error_message2");

    let valid = true;

    if(user_id.trim() === ""){
        error_id.style.display = "block";
        error_id.textContent = "아이디를 입력하세요."
        valid=false;
    } else {
        error_id.style.display ="none";
    }


    if(user_pwd.trim() === ""){
        error_pwd.style.display = "block";
        error_pwd.textContent = "비밀번호를 입력하세요."
        valid=false;
    } else {
        error_pwd.style.display ="none";
    }

    if (valid) {
        document.getElementById("loginForm").submit();  // 폼을 실제로 제출
    }


}