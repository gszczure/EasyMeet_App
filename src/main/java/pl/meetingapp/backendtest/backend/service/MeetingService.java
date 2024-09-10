package pl.meetingapp.backendtest.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.DTO.MeetingParticipantsDTO;
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

    @Autowired
    private DateRangeService dateRangeService;

    public Meeting createMeeting(String name, User owner) {
        Meeting meeting = new Meeting(name, owner);
        return meetingRepository.save(meeting);
    }

    public void saveMeetingDate(Long meetingId, String date) throws Exception {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new Exception("Meeting not found"));
        meeting.setMeetingDate(date);
        meetingRepository.save(meeting);
    }

    public ResponseEntity<String> joinMeeting(String code, String username) {
        // Znajdź spotkanie na podstawie kodu
        Optional<Meeting> meetingOpt = meetingRepository.findByCode(code);
        Meeting meeting = meetingOpt.get();

        //Znajdz uztykownika na podstawie username
        Optional<User> userOpt = userRepository.findByUsername(username);
        User user = userOpt.get();

        if (meeting.getParticipants().contains(user)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        meeting.addParticipant(user);
        meetingRepository.save(meeting);

        return ResponseEntity.ok().build();
    }


    public List<Meeting> getMeetingsForUser(User user) {
        List<Meeting> meetingsAsOwner = meetingRepository.findByOwner(user);
        List<Meeting> meetingsAsParticipant = meetingRepository.findByParticipantsContaining(user);
        meetingsAsOwner.addAll(meetingsAsParticipant);
        return meetingsAsOwner;
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

    public MeetingParticipantsDTO getParticipants(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting with id: " + meetingId + " not found"));

        User owner = meeting.getOwner();
        List<User> participants = new ArrayList<>(meeting.getParticipants());
        participants.add(owner);

        return new MeetingParticipantsDTO(owner, participants);
    }

    public void deleteMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting with id: " + meetingId + " not found"));

        // pobieranie wszystkich DateRanges i usuwanie ich
        List<DateRange> dateRanges = dateRangeService.findByMeetingId(meetingId);
        dateRangeService.deleteAll(dateRanges);

        // Usuwanie spotkania
        meetingRepository.delete(meeting);
    }
}
