let notificationTimeout

function initializeNotification() {
    const notification = document.getElementById("notification")
    const notificationMessage = document.getElementById("notification-message")
    const notificationClose = document.getElementById("notification-close")

    if (!notification || !notificationMessage || !notificationClose) {
        console.error("Notification elements not found. Make sure they are present in the DOM.")
        return
    }

    window.showNotification = (message, duration = 3000, type = "success") => {
        clearTimeout(notificationTimeout)

        notificationMessage.textContent = message

        notification.classList.remove("alert")

        if (type === "alert") {
            notification.classList.add("alert")
        }

        notification.classList.add("show")

        notificationTimeout = setTimeout(() => {
            notification.classList.remove("show")
        }, duration)
    }

    window.showAlert = (message, duration = 4000) => {
        window.showNotification(message, duration, "alert")
    }

    notificationClose.addEventListener("click", () => {
        notification.classList.remove("show")
    })
}
//TODO zrobic to samo dal chmurki komentarza oraz przenisc do osobnego pliku js a nie w notifications to trzymac

// Zegar svg osobna funckja by nie pisac za kazdym razem tego samego podczas tworzenia zegara
function createClockIcon() {
    const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("viewBox", "0 0 24 24");
    svg.classList.add("clock-icon", "icon");

    const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    circle.setAttribute("cx", "12");
    circle.setAttribute("cy", "12");
    circle.setAttribute("r", "10");

    const pointer = document.createElementNS("http://www.w3.org/2000/svg", "polyline");
    pointer.setAttribute("points", "12 6 12 12 16 14");

    svg.appendChild(circle);
    svg.appendChild(pointer);

    return svg;
}

initializeNotification();

