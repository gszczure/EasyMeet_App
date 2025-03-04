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
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
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

    @Autowired
    private MeetingRepository meetingRepository;

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

    // TODO usunac to reczne wpisywanie kodu
    //Zrobione
    // endpoint odpoiwadajacy za dolaczanie uzytkownikow do spotkanie poprzez reczne wpisanie kodu
    @PostMapping("/join")
    public ResponseEntity<String> joinMeeting(@Valid @RequestBody MeetingRequestDTO meetingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return meetingService.joinMeeting(meetingRequest.getCode(), username);
    }

    // Endpoit do wchodzenia do spotkania prze link
    @GetMapping("/join/{code}")
    public ResponseEntity<Void> joinMeeting(@PathVariable String code) {
        Optional<Meeting> meetingOpt = meetingRepository.findByCode(code);

        if (meetingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String frontendUrl = "https://meetme-web-q5ol.onrender.com/date-chose.html?code=" + code;

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl)
                .build();
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
