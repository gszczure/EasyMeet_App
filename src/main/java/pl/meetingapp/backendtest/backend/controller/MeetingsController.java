package pl.meetingapp.backendtest.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.dto.*;
import pl.meetingapp.backendtest.backend.model.*;
import pl.meetingapp.backendtest.backend.service.MeetingDetailsService;
import pl.meetingapp.backendtest.backend.service.MeetingsService;
import pl.meetingapp.backendtest.backend.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetings")
public class MeetingsController {

    @Autowired
    private MeetingsService meetingService;

    @Autowired
    private UserService userService;

    @Autowired
    private MeetingDetailsService dateRangeService;

    //Zrobione
    //Endpoit do tworzenia spotkania (znajduje sie tu juz zapisywanie koomentarza oraz zapisywanie dat)
    @PostMapping("/create")
    public ResponseEntity<MeetingDTO> createMeeting(@RequestBody CreateMeetingRequestDTO createMeetingRequest) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User owner = userService.findByUsername(username);

        Meeting meeting = meetingService.createMeeting(createMeetingRequest.getName(), owner);

        if (createMeetingRequest.getComment() != null && !createMeetingRequest.getComment().isEmpty()) {
            meetingService.addOrUpdateComment(meeting.getId(), createMeetingRequest.getComment().trim());
        }

        if (createMeetingRequest.getDateRanges() != null && !createMeetingRequest.getDateRanges().isEmpty()) {
            List<DateRange> dateRanges = createMeetingRequest.getDateRanges().stream()
                    .map(dto -> {
                        DateRange dateRange = new DateRange();
                        dateRange.setMeeting(meeting);
                        dateRange.setUser(owner);
                        dateRange.setStartDate(dto.getStartDate());
                        dateRange.setStartTime(dto.getStartTime());
                        dateRange.setDuration(dto.getDuration());
                        return dateRange;
                    })
                    .collect(Collectors.toList());

            dateRangeService.saveDateRanges(dateRanges);
        }

        return ResponseEntity.ok(new MeetingDTO(
                meeting.getId(),
                meeting.getName(),
                meeting.getCode(),
                new ParticipantDTO(
                        meeting.getOwner().getId(),
                        meeting.getOwner().getFirstName(),
                        meeting.getOwner().getLastName()
                )
        ));
    }
    //Zrobione
    // endpoint do usuwania spotkania
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(@PathVariable Long meetingId) {
        meetingService.deleteMeeting(meetingId);
        return ResponseEntity.ok().build();
    }
    //Zrobione
    // endpoint odpoiwadajacy za dolaczanie uzytkownikow do spotkania
    @PostMapping("/join")
    public ResponseEntity<String> joinMeeting(@Valid @RequestBody MeetingRequestDTO meetingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return meetingService.joinMeeting(meetingRequest.getCode(), username);
    }
    //Zrobione
    // endpoint do pobierania spotkan dla uzytkownika zalogowanego
    @GetMapping("/for-user")
    public List<MeetingDTO> getMeetingsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return meetingService.getMeetingsForUser(user);
    }

//    //Zrobione
//    @GetMapping("/{meetingId}/participants") // endpoint do pobieranai ludzi ze spotkania i wlasciciela spotkania
//    public ResponseEntity<MeetingParticipantsDTO> getMeetingParticipants(@PathVariable Long meetingId) {
//        try {
//            MeetingParticipantsDTO participantsDTO = meetingService.getParticipants(meetingId);
//            return ResponseEntity.ok(participantsDTO);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }

//    @DeleteMapping("/{meetingId}/participants/{username}")
//    // Endpoit do usuwania kogos z spotkania z walidacja czy jest on wlascicielem spotkania
//    public ResponseEntity<String> removeParticipant(@PathVariable Long meetingId, @PathVariable String username) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String loggedInUsername = authentication.getName();
//        User loggedInUser = userService.findByUsername(loggedInUsername);
//        Meeting meeting = meetingService.findById(meetingId);
//
//        if (meeting == null || !meeting.getOwner().equals(loggedInUser)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        meetingService.removeUserFromMeeting(meetingId, username);
//        User user = userService.findByUsername(username);
//        List<DateRange> dateRanges = dateRangeService.findByUserAndMeeting(user, meetingId);
//        dateRangeService.deleteAll(dateRanges);
//        return ResponseEntity.ok().build();
//    }
//
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
