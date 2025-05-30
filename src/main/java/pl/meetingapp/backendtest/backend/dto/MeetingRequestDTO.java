package pl.meetingapp.backendtest.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingRequestDTO {
//TODO sprawdzic czy ta kalsas jest potrzebna moze w endpoicei samo getmeeting pobrac z meeting jak w endpociie do tworzenai spotkania
    private String name;
    @NotBlank
    private String code;
}
