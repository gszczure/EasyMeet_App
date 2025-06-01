const meetingContainer = document.querySelector('#meeting-container');
const createMeetingButton = document.querySelector('.create-meeting-button');

//TODO dodac isprocessing jak w data-chose.js by blokowac klikanie drugi raz guziak usuwania

// Funkcja do sprawdzania, czy użytkownik jest właścicielem spotkania
function isOwner(ownerId) {
    const currentUserId = localStorage.getItem('userId');
    return currentUserId !== null && currentUserId === ownerId.toString();
}

async function deleteMeeting(meetingId, event) {
    event.stopPropagation();

    const confirmed = confirm("Are you sure you want to delete this meeting?");
    if (!confirmed) return;

    const token = localStorage.getItem('token');
    if (!token) {
        showAlert("You must be logged in to delete a meeting.");
        return;
    }

    try {
        const response = await fetch(`/api/meetings/${meetingId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            }
        });

        if (response.ok) {
            loadMeetings();
        } else {
            showAlert('Failed to delete meeting.');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('An error occurred while deleting the meeting.');
    }
}

// Funkcja dodawania spotkania do UI
function addMeetingToUI(meeting) {
    const meetingCard = document.createElement('div');
    meetingCard.classList.add('meeting-card');

    const dateContainer = document.createElement('div');
    dateContainer.classList.add('date-container');

    const dayDiv = document.createElement('div');
    dayDiv.classList.add('day');

    const monthDiv = document.createElement('div');
    monthDiv.classList.add('month');

    if (meeting.meetingDate) {
        const parsedDate = new Date(meeting.meetingDate);

        if (!isNaN(parsedDate.getTime())) {
            const day = String(parsedDate.getDate()).padStart(2, '0');
            const monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];
            const month = monthNames[parsedDate.getMonth()];

            dayDiv.textContent = day;

            monthDiv.innerHTML = month.split('').map(letter => `<span>${letter}</span>`).join('');

            dateContainer.appendChild(dayDiv);
            dateContainer.appendChild(monthDiv);
        }
    } else {
        const notSelectedDiv = document.createElement('div');
        notSelectedDiv.classList.add('not-selected');
        notSelectedDiv.innerHTML = 'No<br>Date<br>Selected';
        dateContainer.appendChild(notSelectedDiv);
    }

    const content = document.createElement('div');
    content.classList.add('content');

    const nameDiv = document.createElement('div');
    nameDiv.classList.add('meeting-name');
    nameDiv.textContent = meeting.name;
    content.appendChild(nameDiv);

    //Pokazanie svg tylko kiedy komentarz jest w JSON
    if (meeting.comment && meeting.comment.trim() !== '') {
        const commentDiv = document.createElement('div');
        commentDiv.classList.add('info-row', 'comment-row');

        const commentIcon = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        commentIcon.setAttribute("class", "icon comment-icon");
        commentIcon.setAttribute("viewBox", "0 0 24 24");

        const commentPath = document.createElementNS("http://www.w3.org/2000/svg", "path");
        commentPath.setAttribute("d", "M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z");

        commentIcon.appendChild(commentPath);

        const commentText = document.createElement('span');
        commentText.textContent = meeting.comment;

        commentDiv.appendChild(commentIcon);
        commentDiv.appendChild(commentText);
        content.appendChild(commentDiv);
    }

    if (meeting.timeRange && meeting.timeRange.trim() !== '') {
        // Pokazanie svg z godziną
        const timeDiv = document.createElement('div');
        timeDiv.classList.add('info-row', 'time-row');

        const timeIcon = createClockIcon();

        const timeText = document.createElement('span');
        timeText.textContent = meeting.timeRange;

        timeDiv.appendChild(timeIcon);
        timeDiv.appendChild(timeText);
        content.appendChild(timeDiv);
    }

    meetingCard.appendChild(dateContainer);
    meetingCard.appendChild(content);


    if (isOwner(meeting.owner.id)) {
        // TODO &times zrobic zamiast x
        const deleteButton = document.createElement('button');
        deleteButton.classList.add('delete-meeting');
        deleteButton.innerHTML = '×';
        deleteButton.addEventListener('click', (e) => deleteMeeting(meeting.id, e));
        meetingCard.appendChild(deleteButton);
    }

    meetingCard.addEventListener('click', () => {
        // window.location.href = `https://backendmeetmeapp-production.up.railway.app/api/meetings/join/${meeting.code}`;
        window.location.href = `/api/meetings/join/${meeting.code}`;
    });

    meetingContainer.appendChild(meetingCard);
}

// Funkcja do ładowania spotkań
async function loadMeetings() {
    const token = localStorage.getItem("token")

    if (!token) {
        const loginMessage = document.createElement("div");
        loginMessage.classList.add("empty-state");
        loginMessage.innerHTML = `The list of meetings is available only for logged-in users. <br> 
                          I encourage you to <a href="/html/login.html">Log in</a>!`;
        meetingContainer.appendChild(loginMessage);
        return;
    }
    try {
        const response = await fetch(`/api/meetings/for-user`, {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        })

        if (response.ok) {
            const meetings = await response.json()
            meetings.sort((a, b) => a.name.localeCompare(b.name))

            meetingContainer.innerHTML = ""

            if (meetings.length === 0) {
                const emptyState = document.createElement("div")
                emptyState.classList.add("empty-state")
                emptyState.textContent = "You don't have any meetings yet. Create a new meeting to get started."
                meetingContainer.appendChild(emptyState)
            } else {
                meetings.forEach((meeting) => addMeetingToUI(meeting))
            }
        } else if(response.status === 403) {
            const emptyState = document.createElement("div")
            emptyState.classList.add("empty-state")
            emptyState.innerHTML = `The list of meetings is available only for logged-in users. <br> 
                          I encourage you to <a href="/html/login.html">Log in</a>!`;
            meetingContainer.appendChild(emptyState)
        } else {
            showAlert("Failed to load meetings.")
        }
    } catch (error) {
        console.error("Error:", error);
        showAlert("An error occurred while loading the meetings.");
    }
}

createMeetingButton.addEventListener('click', () => {
    window.location.href = '../html/create-meeting.html';
});

// Załaduj spotkania po załadowaniu strony
loadMeetings();