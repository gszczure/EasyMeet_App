package pl.meetingapp.backendtest.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDateRangeDTO {
    @NotNull
    private Long meetingId;
    @NotNull
    private LocalDate startDate;
    private String yesVotes;
    private String duration;
    private String startTime;

//    private LocalDate endDate;
}
