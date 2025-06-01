package pl.meetingapp.backendtest.backend.dto;

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
}
