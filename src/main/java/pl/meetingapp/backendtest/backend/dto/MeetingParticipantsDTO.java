package pl.meetingapp.backendtest.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class MeetingParticipantsDTO {
    private ParticipantDTO owner;
    private List<ParticipantDTO> participants;
}