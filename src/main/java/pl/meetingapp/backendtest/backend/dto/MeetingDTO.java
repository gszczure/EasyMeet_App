package pl.meetingapp.backendtest.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingDTO {
    private Long id;
    private String name;
    private String code;
    private ParticipantDTO owner;
    private String meetingDate;
    private String comment;
    private String timeRange;
}

