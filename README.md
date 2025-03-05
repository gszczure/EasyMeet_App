> [!IMPORTANT]  
> Branch `VersionWeb` zawiera ciągle rozwijaną wersję backendu dla webowej wersji aplikacji.
> 
> Jeśli chcesz zobaczyć pierwszą wersję backendu, stworzoną na potrzeby aplikacji desktopowej, przejdź do branch `main`. **Pamiętaj jednak, że nie jest ona już od dawna wspierana ani rozwijana**.
> 
> W branchu `main` znajdziesz także filmik prezentujący działanie pierwszej wersji aplikacji desktopowej.
> 
> > The English version of the README can be found in the [frontend](https://github.com/gszczure/MeetMe_Web_App) repository.



# MeetMeApp 

MeetMeApp ([Strona WWW](https://meetme-web-q5ol.onrender.com/)) to aplikacja, która upraszcza organizowanie spotkań w grupie, umożliwiając łatwe ustalanie dogodnego terminu dla wszystkich uczestników. Dzięki niej, organizowanie wydarzeń, takich jak spotkania towarzyskie czy wyjazdy, staje się prostsze i bardziej efektywne, eliminując konieczność wymiany wielu wiadomości w celu uzgodnienia terminu.

Poniżej znajdziesz szczegóły dotyczące funkcji oraz technologii, które wykorzystuje MeetMeApp.


## Funkcje

- **Tworzenie wydarzeń**: Tworzący spotkanie musi podać obowiązkową nazwę wydarzenia oraz wybrać co najmniej jedną datę spotkania (organizator może dodać opcjonalny komentarz do spotkania, ale jest to opcja niewymagana do stworzenia wydarzenia). Każda osoba, niezależnie od tego, czy jest zalogowana, czy korzysta z aplikacji jako gość, może tworzyć wydarzenia. Aby stworzyć spotkanie jako gość, wystarczy podać swoje imię i nazwisko — nie jest wymagana rejestracja. Goście mają jednak pewne ograniczenia o ktorych napisałem poniżej.


- **Unikalne linki do wydarzeń**: Każde spotkanie po utworzeniu ma przypisany unikalny link, który można udostępnić innym. Osoby, które otrzymają ten link, mogą dołączyć do wydarzenia, niezależnie od tego, czy są zalogowane, czy korzystają z aplikacji jako gość. Goście muszą jedynie podać swoje imię i nazwisko, aby dołączyć.


- **Głosowanie na daty**: Uczestnicy wydarzenia, po otrzymaniu linku, mogą głosować na dostępne daty, wybierając jedną z opcji: 
  - **Yes** (tak) – jeśli mogą się spotkać w danym terminie.
  - **If needed** (jeśli potrzeba) – jeśli mogą się spotkać tylko wtedy, gdy będzie to konieczne.


- **Ograniczenia dla gości**: Użytkownicy korzystający z aplikacji jako goście mają dostęp do mniej funkcji niż zarejestrowani użytkownicy.
    - Nie mogą zobaczyć listy uczestników wydarzenia.
    - Nie widzą, kto i jak głosował na poszczególne daty spotkania (np. kto zaznaczył, że może się spotkać w danym terminie).
    - Mają dostęp do listy swoich spotkań przez 2 godziny od momentu głosowania, po tym czasie nie będą mogli edytować swoich głosów ani przeglądać listy swoich spotkań.


## Technologie

- **Backend**: Aplikacja oparta jest na technologii **Java** z użyciem **Springa**
- **Frontend**: Kod frontendu aplikacji jest dostępny na GitHubie: [Frontend_MeetMeApp](https://github.com/gszczure/MeetMe_Web_App). Frontend został opracowany przy użyciu **CSS**, **HTML** i **JavaScript**.
- **Autentykacja**: Do autentykacji użytkowników wykorzystano **JWT (JSON Web Token)**, co zapewnia bezpieczne zarządzanie sesjami użytkowników.
- **Baza danych**: Aplikacja korzysta z bazy danych **PostgreSQL**, a hasła użytkowników są **hashowane** przed zapisaniem w bazie danych dla zapewnienia dodatkowej warstwy bezpieczeństwa.
- **Hosting**:
    - Backend aplikacji jest uruchomiony na platformie **Render**.
    - Baza danych znajduje się na platformie **Railway**.

## Uwaga dotycząca wydajności

- Aplikacja może działać **wolno przy pierwszym uruchomieniu**, ponieważ serwer musi się uruchomić. Czas oczekiwania może wynosić nawet 4 minuty podczas logowania lub rejestracji.
- Po uruchomieniu serwera aplikacja powinna działać szybciej.

## Testowanie i uwagi

Aplikacja jest w fazie rozwoju. Wszelkie opinie, sugestie dotyczące ulepszeń, zmian lub usunięć funkcji są mile widziane.

[StronaWWW](https://meetme-web-q5ol.onrender.com/)
