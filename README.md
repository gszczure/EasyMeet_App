> [!IMPORTANT]
> 
> **⚠️ APPLICATION IS CONTINUOUSLY IMPROVED ⚠️️**
> 
> If you want to see the first version of the backend, created for the desktop application, go to the `main` branch. **However, keep in mind that it is no longer maintained or developed.**
>
> In the `main` branch, you will also find a video demonstrating the first version of the desktop application.
>
>
> > Polska wersja README znajduje się na dole.

# EasyMeetApp

## 🌍 English Version

### 🚀 About EasyMeetApp

[EasyMeetApp](https://easymeetapp.up.railway.app) is an application that simplifies scheduling group meetings by helping participants easily find a convenient time. It eliminates the need for excessive messaging, making event planning (such as social gatherings or trips) much more efficient.

Below, you’ll find details about the features and technologies used in EasyMeetApp.

---

### ✨ Features

- **📅 Create Events**: The event organizer must provide a name for the event and select at least one available date. An optional comment can be added, but it’s not required. Anyone, whether logged in or a guest, can create an event. To create an event as a guest, you only need to enter your name—registration is not required. However, guests have some limitations (explained below).

- **🔗 Unique Event Links**: Every created event gets a unique link, which can be shared with others. People who receive the link can join the event, regardless of whether they are logged in or using the app as a guest. Guests only need to provide their name to join.

- **🗳️ Voting on Dates**: Participants can vote on available dates using the following options:
  - **✅ Yes** – if they are available on that date.
  - **🤔 If needed** – if they can attend only if necessary.

- **🚫 Guest Limitations**: Users joining as guests have limited functionality:
  - They **cannot** see the list of participants.
  - They **cannot** see who voted for which date.
  - They can access their event list **only for 2 hours** after voting. After this period, they will no longer be able to edit their votes or view their events.

---

### 🛠️ Technologies

- **Backend**: Built with **Java** and **Spring**.
- **Frontend**: Developed using **HTML**, **CSS**, and **JavaScript**.
- **Authentication**: Uses **JWT (JSON Web Token)** for secure session management.
- **Database**: Utilizes **PostgreSQL**, with user passwords securely **hashed** before storage.
- **Hosting**:
  - Hosted on **Render**.
  - Database runs on **Railway**.

---

### 🧪 Testing

The application uses **JUnit 5** and **Mockito** for unit testing. Currently, there are **54 tests** implemented
**controller and integration tests** are being added regularly as development progresses.

### Test Structure

- **Unit Tests:** These tests cover the core components of the application, such as services and utilities, and are located in the **unitTests package**.


- **Controller Tests:** These tests focus on verifying the behavior of HTTP endpoints and are implemented using **WebMvcTest**. They are located in the **controllerTests package** and are added continuously.

Tests will be added continuously.

---

### ⚠️ Performance Notice

- The application **may be slow on first startup**, as the server needs to wake up. This may take up to **4 minutes**.
- Once the server is running, performance improves significantly.
- I plan to migrate the application to Oracle Cloud to eliminate delays, **but for now, this is not possible due to lack of available database space on Oracle for the VM. 🔧**

---

### 💡 Feedback & Contributions

EasyMeetApp is still in development. Any feedback, suggestions for improvements, or bug reports are highly appreciated! 🚀🎉

---

### 👀 Application Preview

Here are some screenshots of the application in action:
- **How it works**
  ![How_It_works_Final.png](src/main/resources/static/images/How_It_works_Final.png)


- **User meetings**
  ![main_scene.png](src/main/resources/static/images/main_scene.png)


- **Creating a new meeting**
  ![Create_Game_Night.png](src/main/resources/static/images/Create_Game_Night.png)


- **"Game Night" meeting**
  ![Game_Night_Meeting_VoteTimes.png](src/main/resources/static/images/Game_Night_Meeting_VoteTimes.png)
  ![Game_Night_Meeting_MostPopular.png](src/main/resources/static/images/Game_Night_Meeting_MostPopular.png)

---

📌 [Visit EasyMeetApp](https://easymeetapp.up.railway.app) and try it out!

---

## 🇵🇱 Polska Wersja

### 🚀 O EasyMeetApp

[EasyMeetApp](https://easymeetapp.onrender.com/) to aplikacja, która upraszcza organizowanie spotkań w grupie, umożliwiając łatwe ustalanie dogodnego terminu dla wszystkich uczestników. Dzięki niej organizowanie wydarzeń, takich jak spotkania towarzyskie czy wyjazdy, staje się prostsze i bardziej efektywne, eliminując konieczność wymiany wielu wiadomości.

Poniżej znajdziesz szczegóły dotyczące funkcji oraz technologii, które wykorzystuje EasyMeetApp.

---

### ✨ Funkcje

- **📅 Tworzenie wydarzeń**: Organizator wydarzenia musi podać nazwę oraz wybrać co najmniej jedną datę spotkania. Może dodać opcjonalny komentarz, ale nie jest to wymagane. Każdy może tworzyć wydarzenia, niezależnie od tego, czy jest zalogowany, czy działa jako gość. Aby utworzyć spotkanie jako gość, wystarczy podać imię i nazwisko – rejestracja nie jest wymagana. Jednak goście mają pewne ograniczenia (opisane poniżej).

- **🔗 Unikalne linki do wydarzeń**: Każde utworzone wydarzenie ma przypisany unikalny link, który można udostępnić innym. Osoby, które otrzymają ten link, mogą dołączyć do wydarzenia bez konieczności logowania. Goście muszą jedynie podać swoje imię i nazwisko.

- **🗳️ Głosowanie na daty**: Uczestnicy wydarzenia mogą głosować na dostępne daty, wybierając jedną z opcji:
  - **✅ Tak** – jeśli mogą się spotkać w danym terminie.
  - **🤔 Jeśli potrzeba** – jeśli mogą się spotkać tylko w razie konieczności.

- **🚫 Ograniczenia dla gości**:
  - Nie mogą zobaczyć listy uczestników.
  - Nie widzą, kto jak głosował.
  - Mają dostęp do listy swoich spotkań przez **2 godziny** po głosowaniu, po czym tracą możliwość edycji głosów i przeglądania listy wydarzeń.

---

### 🛠️ Technologie

- **Backend**: Aplikacja oparta na **Java + Spring**.
- **Frontend**: Wykorzystuje **HTML, CSS, JavaScript**.
- **Autentykacja**: Bezpieczne zarządzanie sesjami dzięki **JWT (JSON Web Token)**.
- **Baza danych**: **PostgreSQL**, z hasłami użytkowników **hashowanymi** przed zapisaniem.
- **Hosting**:
  - Aplikacja działa na **Render**.
  - Baza danych znajduje się na **Railway**.

---

### 🧪 Testy
Aplikacja wykorzystuje **JUnit 5** oraz **Mockito** do testowania jednostkowego. Obecnie zaimplementowanych jest **35 testów jednostkowych**, a **testy kontrolerów** są regularnie dodawane.

### Struktura Testów

- **Testy jednostkowe:** Te testy obejmują główne komponenty aplikacji, takie jak usługi i narzędzia, i znajdują się w **pakiecie unitTests**.


- **Testy kontrolerów:** Te testy koncentrują się na weryfikowaniu zachowania punktów końcowych HTTP i są zaimplementowane przy użyciu **WebMvcTest**. Znajdują się w **pakiecie controllerTests** i są regularnie dodawane.

Testy są dodawane na bieżąco.

---

### ⚠️ Uwaga dotycząca wydajności

- Aplikacja **może działać wolno przy pierwszym uruchomieniu**, ponieważ serwer musi się uruchomić. Logowanie lub rejestracja może trwać **do 4 minut**.
- Po uruchomieniu serwera aplikacja działa szybciej.
- Planuję przenieść aplikację na Oracle Cloud, aby eliminować opóźnienia, ale na razie jest to **niemożliwe z powodu braku dostępnego miejsca w bazie danych Oracle dla maszyny wirtualnej (VM)**. 🔧


---

### 💡 Opinie i wsparcie

Aplikacja jest w fazie rozwoju. Wszelkie opinie, sugestie dotyczące ulepszeń lub raporty o błędach są mile widziane! 🚀🎉

---

### 👀 Podgląd aplikacji

Oto kilka zrzutów ekranu z aplikacji w akcji:

- **Jak to działa**
  ![How_It_works_Final.png](src/main/resources/static/images/How_It_works_Final.png)

  
- **Spotkania urzytkownika**
  ![main_scene.png](src/main/resources/static/images/main_scene.png)


- **Tworzenie nowego spotkania**
  ![Create_Game_Night.png](src/main/resources/static/images/Create_Game_Night.png)


- **Spotkanie "Wieczór Gier"**
  ![Game_Night_Meeting_VoteTimes.png](src/main/resources/static/images/Game_Night_Meeting_VoteTimes.png)
  ![Game_Night_Meeting_MostPopular.png](src/main/resources/static/images/Game_Night_Meeting_MostPopular.png)

---

📌 [Sprawdź EasyMeetApp](https://easymeetapp.onrender.com/)!
