package pl.meetingapp.backendtest.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingRequestDTO {

    // Klasa DTO ktora z JSON wybiera tylko name po to by potem zapisac je w tabeli meetings
    // oraz do pobierania code i uzywania go w /join
    private String name;
    @NotBlank
    private String code;
}
