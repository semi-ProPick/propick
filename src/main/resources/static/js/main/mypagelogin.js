function checkLogin() {
    let isLoggedIn = localStorage.getItem("isLoggedIn");

    if (!isLoggedIn) {
        document.getElementById("login-popup").style.display = "flex";
    }
}

function closePopup() {
    document.getElementById("login-popup").style.display = "none";
}

function redirectToLogin() {
    window.location.href = "login.html";
}
