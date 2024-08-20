package pl.meetingapp.backendtest.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
import pl.meetingapp.backendtest.backend.repository.UserRepository;

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
}
