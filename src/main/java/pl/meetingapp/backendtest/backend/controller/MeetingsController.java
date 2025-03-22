package pl.meetingapp.backendtest.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.dto.*;
import pl.meetingapp.backendtest.backend.model.*;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
import pl.meetingapp.backendtest.backend.service.MeetingsService;
import pl.meetingapp.backendtest.backend.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingsController {

    private final MeetingsService meetingService;

    private final UserService userService;

    private final MeetingRepository meetingRepository;

    //Zrobione
    //Endpoit do tworzenia spotkania (znajduje sie tu juz zapisywanie koomentarza oraz zapisywanie dat)
    @PostMapping("/create")
    public ResponseEntity<MeetingDTO> createMeeting(@RequestBody CreateMeetingRequestDTO createMeetingRequest) throws Exception {
        MeetingDTO meetingDTO = meetingService.createMeeting(createMeetingRequest);
        return ResponseEntity.ok(meetingDTO);
    }

    //Zrobione
    // endpoint do usuwania spotkania
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(@PathVariable Long meetingId) {
        meetingService.deleteMeeting(meetingId);
        return ResponseEntity.ok().build();
    }

    //Zrobione
    // endpoint odpoiwadajacy za dolaczanie uzytkownikow do spotkania (autojoin)
    @PostMapping("/join")
    public ResponseEntity<String> joinMeeting(@Valid @RequestBody MeetingRequestDTO meetingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return meetingService.joinMeeting(meetingRequest.getCode(), username);
    }

    // Endpoit do wchodzenia do doalczenia do spotkania poprzez link
    @GetMapping("/join/{code}")
    public ResponseEntity<Void> joinMeeting(@PathVariable String code) {
        Optional<Meeting> meetingOpt = meetingRepository.findByCode(code);

        if (meetingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String frontendUrl = "/html/date-chose.html?code=" + code;

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl)
                .build();
    }


    //Zrobione
    // endpoint do pobierania spotkan dla uzytkownika zalogowanego
    @GetMapping("/for-user")
    public ResponseEntity<?> getMeetingsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user.isGuest()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Guest users cannot access this endpoint.");
        }

        List<MeetingDTO> meetings = meetingService.getMeetingsForUser(user);
        return ResponseEntity.ok(meetings);
    }

    // TODO przeniesc te endpoity dwa do MeetingDatailsController
    //ZROBIONE
    // Endpoit do pobierania ludzi dla jakiesgos spotkania
    @GetMapping("/{meetingId}/participants")
    public ResponseEntity<MeetingParticipantsDTO> getMeetingParticipants(@PathVariable Long meetingId) {
        try {
            MeetingParticipantsDTO participantsDTO = meetingService.getParticipants(meetingId);
            return ResponseEntity.ok(participantsDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // TODO zrobic testy usuwanajace ludzi czyli czy jak sie usuwa spotkanie lu ludzi to czy wszystko powiazane z nim leci
    //ZROBIONE
    // Endpoit do usuwania uzytkownik z spotkania (tylkko wlasciceil)
    @DeleteMapping("/{meetingId}/participants/{userId}")
    public ResponseEntity<String> removeParticipant(@PathVariable Long meetingId, @PathVariable Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();
        User loggedInUser = userService.findByUsername(loggedInUsername);
        Meeting meeting = meetingService.findById(meetingId);

        if (meeting == null || !meeting.getOwner().equals(loggedInUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean removed = meetingService.removeUserFromMeeting(meetingId, userId);
        if (removed) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

//
//    @DeleteMapping("/{meetingId}/leave") // Endpoint do opuszczania spotkania przez uczestnika
//    public ResponseEntity<String> leaveMeeting(@PathVariable Long meetingId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        meetingService.removeUserFromMeeting(meetingId, username);
//        User user = userService.findByUsername(username);
//        List<DateRange> dateRanges = dateRangeService.findByUserAndMeeting(user, meetingId);
//        dateRangeService.deleteAll(dateRanges);
//        return ResponseEntity.ok().build();
//    }
}
