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

//    public Long getMeetingId() {
//        return meetingId;
//    }
//
//    public void setMeetingId(Long meetingId) {
//        this.meetingId = meetingId;
//    }
//
//    public LocalDate getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(LocalDate startDate) {
//        this.startDate = startDate;
//    }
//
//    public LocalDate getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(LocalDate endDate) {
//        this.endDate = endDate;
//    }
}
