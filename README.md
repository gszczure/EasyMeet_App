# MeetMeApp

MeetMeApp to aplikacja do organizowania spotkań, umożliwiająca użytkownikom tworzenie i dołączanie do wydarzeń, takich jak na przykład „BBQ w Pisarach”. Aplikacja powstała z potrzeby znalezienia dogodnej daty spotkania dla większej grupy znajomych, gdzie każdy miał różne preferencje czasowe. Dzięki niej, znalezienie wspólnego na terminu na spotkanie stało się znacznie prostsze.

Poniżej znajdziesz szczegóły dotyczące instalacji, technologii oraz podstawowych funkcji aplikacji.

## Funkcje

- **Tworzenie spotkań**: Zarejestrowani użytkownicy mogą tworzyć własne wydarzenia.
- **Udostępnianie kodów**: Każde wydarzenie generuje unikalny kod, który można udostępnić innym użytkownikom. Wpisując ten kod, mogą oni dołączyć do wydarzenia.
- **Zarządzanie uczestnikami**: Właściciel spotkania może usuwać uczestników, a uczestnicy mogą opuszczać wydarzenie dobrowolnie.
- **Wybór dat spotkania**:
  - Użytkownicy mogą dodawać dowolną liczbę przedziałów dat, które im odpowiadają.
  - Każdy użytkownik może **usunąć tylko swoje przedziały dat**, a nie przedziały innych uczestników.
  - Aplikacja pokazuje wspólne dostępne daty dla wszystkich uczestników spotkania. Właściciel spotkania ma możliwość wyboru ostatecznej daty spośród dostępnych opcji.

## Technologie

- **Backend**: Aplikacja oparta jest na technologii **Java** z użyciem **Springa** i wymaga minimum wersji 14 (zalecana Oracle Java SDK 19).
- **Frontend**: Kod frontendu aplikacji jest dostępny na GitHubie: [FRONTENDMEETINGAPP](https://github.com/gszczure/FRONTENDMEETINGAPP). Frontend został opracowany za pomocą JavaFX i Scene Builder, z użyciem CSS do stylizacji.
- **Autentykacja**: Do autentykacji użytkowników wykorzystano **JWT (JSON Web Token)**, co zapewnia bezpieczne zarządzanie sesjami użytkowników.
- **Baza danych**: Aplikacja korzysta z bazy danych **PostgreSQL**, a hasła użytkowników są **hashowane** przed zapisaniem w bazie danych dla zapewnienia dodatkowej warstwy bezpieczeństwa.
- **Hosting**:
  - Backend aplikacji jest uruchomiony na platformie **Render**.
  - Baza danych znajduje się na platformie **Railway**.

## Zobacz demonstrację działania pierwszej wersji aplikacji

[Zobacz demonstrację na YouTube](https://youtu.be/fVYEp7d8_mM)

## Wymagania systemowe

Aby uruchomić aplikację, wymagane jest:
- **Java** w wersji minimum 14 (zalecana Oracle Java SDK 19).

Sprawdź instalację Javy za pomocą polecenia:
`java -version`

## Instalacja

1. Pobierz i zainstaluj aplikację.
2. Aplikacja instaluje się domyślnie w katalogu `C:\\ProgramFiles\\MeetMeApp`.
3. Znajdź plik `.exe` w folderze instalacyjnym. **Nie przenoś go bezpośrednio na pulpit!** Zamiast tego, utwórz skrót do tego pliku na pulpicie, aby aplikacja działała poprawnie.

## Uwaga dotycząca wydajności

- Aplikacja może działać **wolno przy pierwszym uruchomieniu**, ponieważ serwer musi się uruchomić. Czas oczekiwania może wynosić nawet 4 minuty podczas logowania lub rejestracji.
- Po uruchomieniu serwera aplikacja powinna działać szybciej.

## Testowanie i uwagi

Aplikacja jest w fazie rozwoju. Wszelkie opinie, sugestie dotyczące ulepszeń, zmian lub usunięć funkcji są mile widziane.

Dostępne jest testowe spotkanie o kodzie: `af86a`.
