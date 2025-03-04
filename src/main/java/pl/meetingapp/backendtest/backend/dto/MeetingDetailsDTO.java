package pl.meetingapp.backendtest.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MeetingDetailsDTO {
    private Long meetingId;
    private String name;
    private String owner;
    private Long ownerId;
    private String comment;
    private List<MeetingDateRangeDTO> dateRanges;
    private boolean isGuest;
}

