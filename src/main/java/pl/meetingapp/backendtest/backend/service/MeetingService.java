package pl.meetingapp.backendtest.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
import pl.meetingapp.backendtest.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    public Meeting createMeeting(String name, User owner) {
        Meeting meeting = new Meeting(name, owner);
        return meetingRepository.save(meeting);
    }

    //TODO: zapisuje sie jako caly obkient json zmienc to
    public void saveMeetingDate(Long meetingId, String date) throws Exception {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new Exception("Meeting not found"));
        meeting.setMeetingDate(date);
        meetingRepository.save(meeting);
    }

    public ResponseEntity<String> joinMeeting(String code, String username) {
        Optional<Meeting> meetingOpt = meetingRepository.findByCode(code);
        if (meetingOpt.isPresent()) {
            Meeting meeting = meetingOpt.get();
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                meeting.addParticipant(user);
                meetingRepository.save(meeting);
                return ResponseEntity.ok("Joined the meeting.");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to join.");
    }

    public List<Meeting> getMeetingsForUser(User user) {
        List<Meeting> meetingsAsOwner = meetingRepository.findByOwner(user);
        List<Meeting> meetingsAsParticipant = meetingRepository.findByParticipantsContaining(user);
        meetingsAsOwner.addAll(meetingsAsParticipant);
        return meetingsAsOwner;
    }

    public List<User> getParticipants(Long meetingId) {
        List<Long> participantIds = meetingRepository.findParticipantIdsByMeetingId(meetingId);
        List<User> participants = new ArrayList<>(userRepository.findAllById(participantIds));
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new RuntimeException("Meeting witch id: " + meetingId + " not found"));
        User owner = meeting.getOwner();
        participants.add(owner);
        return participants;
    }
    public Meeting findById(Long id) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        return meeting.orElse(null);
    }
    public void removeUserFromMeeting(Long meetingId, String username) {
        Meeting meeting = findById(meetingId);
        if (meeting != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                meeting.getParticipants().remove(user);

                meetingRepository.save(meeting);
            }
        }
    }

}
