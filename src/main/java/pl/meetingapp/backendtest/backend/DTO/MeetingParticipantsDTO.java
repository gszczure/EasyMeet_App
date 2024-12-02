package pl.meetingapp.backendtest.backend.DTO;

import lombok.Getter;
import lombok.Setter;
import pl.meetingapp.backendtest.backend.model.User;

import java.util.List;

@Getter
@Setter
public class MeetingParticipantsDTO {
    private User owner;
    private List<User> participants;

    public MeetingParticipantsDTO(User owner, List<User> participants) {
        this.owner = owner;
        this.participants = participants;
    }
}

