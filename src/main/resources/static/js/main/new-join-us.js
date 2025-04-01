async function checkDuplicate(endpoint, param) {
    try {
        const response = await fetch(`/api/user/${endpoint}?${param}`);
        const isDuplicate = await response.json();
        return isDuplicate;
    } catch (error) {
        console.error(`중복 검사 오류 (${endpoint}):`, error);
        return false; // 오류 발생 시 중복이 아닌 것으로 간주
    }
}

async function validateForm() {
    let user_name = document.getElementById("insert_name").value.trim();
    let user_phone = document.getElementById("insert_phone").value.trim();
    let user_id = document.getElementById("insert_id").value.trim();
    let user_pwd = document.getElementById("insert_pwd").value.trim();
    let user_confirm_pwd = document.getElementById("insert_confirm_pwd").value.trim();
    let user_birth = document.getElementById("insert_birth").value.trim();

    let error_name = document.getElementById("error_message1");
    let error_phone = document.getElementById("error_message2");
    let error_id = document.getElementById("error_message3");
    let error_pwd = document.getElementById("error_message4");
    let error_confirm = document.getElementById("error_message5");
    let error_birth = document.getElementById("error_message6");

    let koreanRegex = /^[가-힣]+$/;
    let numberRegex1 = /^[0-9]{10,11}$/;
    let dateRegex = /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/;
    let engNum = /^[a-zA-Z0-9]*$/;
    let passwordPattern = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~@#$!%*?&])[a-zA-Z\d~@#$!%*?&]{8,}$/;

    let valid = true;

    // 이름 유효성 검사
    if (user_name === "") {
        error_name.style.display = "block";
        error_name.textContent = "이름을 입력해주세요.";
        valid = false;
    } else if (!koreanRegex.test(user_name)) {
        error_name.style.display = "block";
        error_name.textContent = "이름을 제대로 입력해주세요.";
        valid = false;
    } else {
        error_name.style.display = "none";
    }

    // 연락처 유효성 검사
    if (user_phone === "") {
        error_phone.style.display = "block";
        error_phone.textContent = "연락처를 입력해주세요.";
        valid = false;
    } else if (!numberRegex1.test(user_phone)) {
        error_phone.style.display = "block";
        error_phone.textContent = "연락처를 다시 확인해주세요.";
        valid = false;
    } else {
        error_phone.style.display = "none";
    }

    // 아이디 유효성 검사
    if (user_id === "") {
        error_id.style.display = "block";
        error_id.textContent = "ID를 입력해주세요.";
        valid = false;
    } else if (!engNum.test(user_id)) {
        error_id.style.display = "block";
        error_id.textContent = "영어와 숫자만 가능합니다.";
        valid = false;
    } else {
        error_id.style.display = "none";
    }

    // 비밀번호 유효성 검사
    if (user_pwd === "") {
        error_pwd.style.display = "block";
        error_pwd.textContent = "비밀번호를 입력하세요.";
        valid = false;
    } else if (!passwordPattern.test(user_pwd)) {
        error_pwd.style.display = "block";
        error_pwd.textContent = "영어, 특수문자, 숫자를 포함시키고 8자리 이상으로 입력하세요.";
        valid = false;
    } else {
        error_pwd.style.display = "none";
    }

    // 비밀번호 확인 검사
    if (user_confirm_pwd === "") {
        error_confirm.style.display = "block";
        error_confirm.textContent = "비밀번호를 한번 더 입력해주세요.";
        valid = false;
    } else if (user_pwd !== user_confirm_pwd) {
        error_confirm.style.display = "block";
        error_confirm.textContent = "비밀번호가 일치하지 않습니다.";
        valid = false;
    } else {
        error_confirm.style.display = "none";
    }

    // 생년월일 검사
    if (user_birth === "") {
        error_birth.style.display = "block";
        error_birth.textContent = "생년월일을 입력해주세요.";
        valid = false;
    } else if (!dateRegex.test(user_birth)) {
        error_birth.style.display = "block";
        error_birth.textContent = "0000-00-00 형식으로 입력해주세요.";
        valid = false;
    } else {
        error_birth.style.display = "none";
    }

    // 모든 기본 유효성 검사를 통과한 경우, 중복 검사 실행
    if (valid) {
        const isPhoneDuplicate = await checkDuplicate("check-id", `userPhone=${user_phone}`);
        const isIdDuplicate = await checkDuplicate("check-id", `userId=${user_id}`);

        if (isPhoneDuplicate) {
            error_phone.style.display = "block";
            error_phone.textContent = "이미 사용 중인 전화번호입니다.";
            valid = false;
        }

        if (isIdDuplicate) {
            error_id.style.display = "block";
            error_id.textContent = "이미 사용 중인 아이디입니다.";
            valid = false;
        }

        // 모든 조건을 통과하면 폼 제출
        if (valid) {
            document.getElementById("joinForm").submit();
        }
    }
}
