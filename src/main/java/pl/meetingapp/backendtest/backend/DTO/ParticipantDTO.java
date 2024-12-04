package pl.meetingapp.backendtest.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//Klasa DTo po to by JSON nie zwracal mi wszystkich danych o uzytkowniku a tylko te ktorych potrzebuje bo latwo mozna ukrasc dane kiedy by tak nie bylo
// teraz zwraca tylko id firstname i lastname, ta klasa jest potrzebna do klasy MeetingParticipantsDTO oraz MeetingDTO

@Setter
@Getter
@AllArgsConstructor
public class ParticipantDTO {
    private Long id;
    private String firstName;
    private String lastName;
}
