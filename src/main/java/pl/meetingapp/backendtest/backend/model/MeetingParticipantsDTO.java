package pl.meetingapp.backendtest.backend.model;

import java.util.List;

public class MeetingParticipantsDTO {
    private User owner;
    private List<User> participants;

    public MeetingParticipantsDTO(User owner, List<User> participants) {
        this.owner = owner;
        this.participants = participants;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }
}

