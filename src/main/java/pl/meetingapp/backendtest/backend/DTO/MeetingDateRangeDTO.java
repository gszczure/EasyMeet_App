package pl.meetingapp.backendtest.backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MeetingDateRangeDTO {
    private long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String addedBy;
    private long userId;


    public MeetingDateRangeDTO(long id, LocalDate startDate, LocalDate endDate, String addedBy, long userId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.addedBy = addedBy;
        this.userId = userId;
    }
}
