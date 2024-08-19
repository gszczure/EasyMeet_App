package pl.meetingapp.backendtest.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.MeetingRequest;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.service.MeetingService;
import pl.meetingapp.backendtest.backend.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private UserService userService;

    @PostMapping("/create") // edpoint do tworzenia spotkania
    public Meeting createMeeting(@RequestBody MeetingRequest meetingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User owner = userService.findByUsername(username);

        String name = meetingRequest.getName();

        return meetingService.createMeeting(name, owner);
    }

    @PostMapping("/join") // edpoint do doalczania do spotkania
    public Optional<Meeting> joinMeeting(@RequestParam String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return meetingService.joinMeeting(code, username);
    }
    @GetMapping("/user") // edpoint do pobierania spotkan dla uzytkownika
    public List<Meeting> getMeetingsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return meetingService.getMeetingsByOwner(user);
    }
}
