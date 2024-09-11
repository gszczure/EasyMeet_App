package pl.meetingapp.backendtest.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.DTO.DateRequestDTO;
import pl.meetingapp.backendtest.backend.DTO.MeetingParticipantsDTO;
import pl.meetingapp.backendtest.backend.DTO.MeetingRequestDTO;
import pl.meetingapp.backendtest.backend.model.*;
import pl.meetingapp.backendtest.backend.service.DateRangeService;
import pl.meetingapp.backendtest.backend.service.MeetingService;
import pl.meetingapp.backendtest.backend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private UserService userService;

    @Autowired
    private DateRangeService dateRangeService;

    @PostMapping("/create") // endpoint do tworzenia spotkania
    public Meeting createMeeting(@RequestBody MeetingRequestDTO meetingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User owner = userService.findByUsername(username);

        String name = meetingRequest.getName();

        return meetingService.createMeeting(name, owner);
    }

    @DeleteMapping("/{meetingId}") // endpoint do usuwania spotkania
    public ResponseEntity<String> deleteMeeting(@PathVariable Long meetingId) {
            meetingService.deleteMeeting(meetingId);
            return ResponseEntity.ok().build();
    }

    @PostMapping("/join") // endpoint odpoiwadajacy za dolaczanie uzytkownikow do spotkania
    public ResponseEntity<String> joinMeeting(@Valid @RequestBody MeetingRequestDTO meetingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return meetingService.joinMeeting(meetingRequest.getCode(), username);
    }

    @GetMapping("/for-user") // endpoint odpowiadajacy za pobieranie spotkan w ktorych naleza uczestnicy lub ktore zalozyli
    public List<Meeting> getMeetingsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return meetingService.getMeetingsForUser(user);
    }

    @GetMapping("/{meetingId}/participants") // endpoint do pobieranai ludzi ze spotkania i wlasciciela spotkania
    public ResponseEntity<MeetingParticipantsDTO> getMeetingParticipants(@PathVariable Long meetingId) {
        try {
            MeetingParticipantsDTO participantsDTO = meetingService.getParticipants(meetingId);
            return ResponseEntity.ok(participantsDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{meetingId}/participants/{username}") // Endpoit do usuwania kogos z spotkania z walidacja czy jest on wlascicielem spotkania
    public ResponseEntity<String> removeParticipant(@PathVariable Long meetingId, @PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();
        User loggedInUser = userService.findByUsername(loggedInUsername);
        Meeting meeting = meetingService.findById(meetingId);

        if (meeting == null || !meeting.getOwner().equals(loggedInUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        meetingService.removeUserFromMeeting(meetingId, username);
        User user = userService.findByUsername(username);
        List<DateRange> dateRanges = dateRangeService.findByUserAndMeeting(user, meetingId);
        dateRangeService.deleteAll(dateRanges);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{meetingId}/leave") // Endpoint do opuszczania spotkania przez uczestnika
    public ResponseEntity<String> leaveMeeting(@PathVariable Long meetingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        meetingService.removeUserFromMeeting(meetingId, username);
        User user = userService.findByUsername(username);
        List<DateRange> dateRanges = dateRangeService.findByUserAndMeeting(user, meetingId);
        dateRangeService.deleteAll(dateRanges);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{meetingId}/date") // endpoint pobiera datę spotkania (czyli odczytuje datę przypisaną do danego spotkania)
    public ResponseEntity<String> getMeetingDate(@PathVariable Long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(meeting.getMeetingDate());
    }

    @PostMapping("/{meetingId}/date") //endpoint ustawia datę spotkania, czyli umożliwia zaktualizowanie daty przez własciela
    public ResponseEntity<?> setMeetingDate(@Valid @PathVariable Long meetingId, @RequestBody DateRequestDTO dateRequest) {
        try {
            String date = dateRequest.getDate();
            meetingService.saveMeetingDate(meetingId, date);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
