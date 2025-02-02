package pl.meetingapp.backendtest.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ParticipantDTO {
    private Long id;
    private String firstName;
    private String lastName;

    @JsonIgnore
    private String username;

    // Nie pełny konstruktor poniewaz nie chce by usernema byl wyswietlany w JSON podczas pobierania spotkań dla uzytkownika
    public ParticipantDTO(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
