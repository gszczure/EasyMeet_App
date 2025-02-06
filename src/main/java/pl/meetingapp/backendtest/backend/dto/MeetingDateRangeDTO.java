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

    public MeetingDateRangeDTO(long id, LocalDate startDate, String startTime, String duration) {
        this.id = id;
        this.startDate = startDate;
        this.startTime = startTime;
        this.duration = duration;
    }
}
