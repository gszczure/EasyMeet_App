package pl.meetingapp.backendtest.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateMeetingRequestDTO {
    @NotBlank(message = "Meeting name cannot be empty")
    private String name;

    private String comment;

    private List<CreateDateRangeDTO> dateRanges;
}
