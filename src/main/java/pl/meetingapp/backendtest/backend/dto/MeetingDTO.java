package pl.meetingapp.backendtest.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MeetingDTO {
    private Long id;
    private String name;
    private String code;
    private ParticipantDTO owner;
}

