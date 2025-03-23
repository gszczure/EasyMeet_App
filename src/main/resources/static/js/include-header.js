fetch("/header.html")
    .then((response) => response.text())
    .then((data) => {
        document.getElementById("header-container").innerHTML = data;
        initMenu();
        checkToken();
    })
    .catch((error) => console.error("Error loading header:", error));

function initMenu() {
    document.querySelector(".hamburger-menu").addEventListener("click", function () {
        document.body.classList.add("menu-open");
    });

    document.querySelector(".overlay").addEventListener("click", function () {
        document.body.classList.remove("menu-open");
    });

    document.querySelector(".close-menu").addEventListener("click", function () {
        document.body.classList.remove("menu-open");
    });
}

function checkToken() {
    const token = localStorage.getItem("token");
    const userData = token ? parseJwt(token) : null;

    const logoutButtons = document.querySelectorAll(".logout")
    const loginLinks = document.querySelectorAll(".login")
    const signupLinks = document.querySelectorAll(".signup")


    if (!userData?.isGuest && token) {
        loginLinks.forEach((link) => {
            link.style.display = "none"
        })

        logoutButtons.forEach((btn) => {
            btn.style.display = "block"
            btn.addEventListener("click", logout)
        })

        signupLinks.forEach((link) => {
            link.style.display = "none"
        })
    }
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        return JSON.parse(atob(base64));
    } catch (error) {
        return null;
    }
}

function logout() {
    localStorage.clear();
    window.location.href = "/index.html";
}
