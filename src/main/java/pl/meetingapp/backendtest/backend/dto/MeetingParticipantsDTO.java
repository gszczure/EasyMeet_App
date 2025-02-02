package pl.meetingapp.backendtest.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

//Teraz moj JSON jest tylko z najwazniejszymi inforamcjami czyli id imie i nazwisko zamiast wszystkicj inforamcji Users
@Getter
@Setter
@AllArgsConstructor
public class MeetingParticipantsDTO {
    private ParticipantDTO owner;
    private List<ParticipantDTO> participants;
}

