package pl.meetingapp.backendtest.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MeetingDateRangeDTO {
    private long id;
    private LocalDate startDate;
    private String startTime;
    private String duration;
    private String addedBy;
    private long userId;

    public MeetingDateRangeDTO(long id, LocalDate startDate, String startTime, String duration, String addedBy, long userId) {
        this.id = id;
        this.startDate = startDate;
        this.startTime = startTime;
        this.duration = duration;
        this.addedBy = addedBy;
        this.userId = userId;
    }
}
