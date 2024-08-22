package pl.meetingapp.backendtest.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.MeetingRequest;
import pl.meetingapp.backendtest.backend.model.User;
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

    @PostMapping("/create") // endpoint do tworzenia spotkania
    public Meeting createMeeting(@RequestBody MeetingRequest meetingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User owner = userService.findByUsername(username);

        String name = meetingRequest.getName();

        return meetingService.createMeeting(name, owner);
    }
    @PostMapping("/join") // endpoint odpoiwadajacy za dodawanie uzytkownikow
    public ResponseEntity<String> joinMeeting(@RequestBody MeetingRequest meetingRequest) {
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

    @GetMapping("/{meetingId}/participants") //endpoint pobierajacy liste uczestnikow spotkania razem z wlascicielem
    public ResponseEntity<List<User>> getParticipants(@PathVariable Long meetingId) {
        List<User> participants = meetingService.getParticipants(meetingId);
        return ResponseEntity.ok(participants);
    }
}
