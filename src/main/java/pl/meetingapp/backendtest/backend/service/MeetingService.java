package pl.meetingapp.backendtest.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
import pl.meetingapp.backendtest.backend.repository.UserRepository;

import java.util.Optional;

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

    public Optional<Meeting> joinMeeting(String code, String username) {
        Optional<Meeting> meetingOpt = meetingRepository.findByCode(code);
        if (meetingOpt.isPresent()) {
            Meeting meeting = meetingOpt.get();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
            meeting.addParticipant(user);
            meetingRepository.save(meeting);
            return Optional.of(meeting);
        }
        return Optional.empty();
    }
}
