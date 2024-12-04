package pl.meetingapp.backendtest.backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class DateRangeDTO {
    private Long meetingId;
    private LocalDate startDate;
    private LocalDate endDate;
}
