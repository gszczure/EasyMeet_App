package pl.meetingapp.backendtest.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DateRequestDTO {

    @NotBlank
    private String date;
}
