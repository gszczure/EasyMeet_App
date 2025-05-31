package pl.meetingapp.backendtest.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingDateRangeDTO {
    private long id;
    private LocalDate startDate;
    private String timeRange;
}
