package pl.meetingapp.backendtest.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import pl.meetingapp.backendtest.backend.security.JwtTokenUtil;
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

    private final JwtTokenUtil jwtTokenUtil;

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
    public ResponseEntity<?> getMeetingsForUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = jwtTokenUtil.removeBearerPrefix(authHeader);

        if (jwtTokenUtil.isGuest(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Guest users cannot access this endpoint.");
        }

        String username = jwtTokenUtil.extractUsername(token);

        User user = userService.findByUsername(username);

        List<MeetingDTO> meetings = meetingService.getMeetingsForUser(user);
        return ResponseEntity.ok(meetings);
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
