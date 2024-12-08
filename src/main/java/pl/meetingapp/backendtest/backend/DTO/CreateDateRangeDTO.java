package pl.meetingapp.backendtest.backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDateRangeDTO {
    private Long meetingId;
    private LocalDate startDate;
    private LocalDate endDate;
}
