package pl.meetingapp.backendtest.backend.DTO;

import jakarta.validation.constraints.NotBlank;

public class MeetingRequestDTO {

    // Klasa DTO ktora z JSON wybiera tylko name po to by potem zapisac je w tabeli meetings
    // oraz do pobierania code i uzywania go w /join
    private String name;
    @NotBlank
    private String code;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
