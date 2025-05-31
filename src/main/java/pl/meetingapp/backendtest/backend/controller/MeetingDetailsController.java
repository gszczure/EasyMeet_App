package pl.meetingapp.backendtest.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.dto.MeetingDateRangeDTO;
import pl.meetingapp.backendtest.backend.dto.MeetingDetailsDTO;
import pl.meetingapp.backendtest.backend.dto.MeetingParticipantsDTO;
import pl.meetingapp.backendtest.backend.dto.VoteInfo;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.security.JwtTokenUtil;
import pl.meetingapp.backendtest.backend.service.MeetingDetailsService;
import pl.meetingapp.backendtest.backend.service.MeetingsService;
import pl.meetingapp.backendtest.backend.service.UserService;
import pl.meetingapp.backendtest.backend.service.VoteService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.meetingapp.backendtest.backend.service.MeetingDetailsService.calculateTimeRange;

@RestController
@RequestMapping("/api/meeting-details")
@RequiredArgsConstructor
public class MeetingDetailsController {

    private final MeetingDetailsService meetingDetailsService;

    private final MeetingsService meetingService;

    private final VoteService voteService;

    private final UserService userService;

    private final JwtTokenUtil jwtTokenUtil;

    //ZROBIONE
    // Endpoit do pobierania wszystkich potrzebnych informacji po wejsciu na strone gdzie uzytkownicy wybieraja daty
    @GetMapping("/details/{code}")
    public ResponseEntity<MeetingDetailsDTO> getMeetingDetails(
            @PathVariable String code,
            @RequestHeader("Authorization") String token) {

        //TODO przeniesc to do service a nie w controllerze robic
        Optional<Meeting> meetingOptional = meetingService.getMeetingByCode(code);
        if (meetingOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Meeting meeting = meetingOptional.get();
        MeetingDetailsDTO detailsDTO = new MeetingDetailsDTO();
        detailsDTO.setMeetingId(meeting.getId());
        detailsDTO.setName(meeting.getName());
        detailsDTO.setOwner(meeting.getOwner().getFirstName() + " " + meeting.getOwner().getLastName());
        detailsDTO.setOwnerId(meeting.getOwner().getId());
        detailsDTO.setComment(meeting.getComment());

        List<MeetingDateRangeDTO> dateRanges = meetingDetailsService.findDateRangesByMeetingId(meeting.getId()).stream()
                .map(dateRange -> {
                    String timeRange = calculateTimeRange(
                            dateRange.getStartDate(),
                            dateRange.getStartTime(),
                            dateRange.getDuration()
                    );

                    return new MeetingDateRangeDTO(
                            dateRange.getId(),
                            dateRange.getStartDate(),
                            timeRange
                    );
                })
                .collect(Collectors.toList());

        detailsDTO.setDateRanges(dateRanges);

        String username = jwtTokenUtil.extractUsername(token.replace("Bearer ", ""));
        User user = userService.findByUsername(username);

        detailsDTO.setGuest(user.isGuest());

        return ResponseEntity.ok(detailsDTO);
    }

    // ZROBIONE
    // Endpoit do pobieranai jak ludzie glosowali na dane spotkanie (Sprawdzicz to )
    @GetMapping("/getVotes/{dateRangeId}")
    public ResponseEntity<?> getVotes(@PathVariable Long dateRangeId,
                                      @RequestHeader(value = "Authorization") String authHeader) {

        String token = jwtTokenUtil.removeBearerPrefix(authHeader);

        if (jwtTokenUtil.isGuest(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Guest users cannot access vote data.");
        }

        List<VoteInfo> votes = voteService.getVotesForDateRange(dateRangeId);

        return ResponseEntity.ok(votes);
    }

    // ZROBONE
    // Endpoit do zapisywania final dat dla spotkan
    @PostMapping("/{meetingId}/save-date")
    public ResponseEntity<?> saveMeetingDate(
            @PathVariable Long meetingId,
            @RequestBody Map<String, String> payload,
            @RequestHeader(value = "Authorization") String authHeader) {

        try {

            String token = jwtTokenUtil.removeBearerPrefix(authHeader);
            String currentUsername = jwtTokenUtil.extractUsername(token);

            Optional<Meeting> meetingOptional = meetingDetailsService.findMeetingById(meetingId);
            if (meetingOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Meeting meeting = meetingOptional.get();

            if (!meetingDetailsService.isUserOwner(currentUsername, meeting)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only the meeting owner can save the meeting date");
            }

            String meetingDate = payload.get("meetingDate");

            if (meetingDetailsService.isInvalidDate(meetingDate)) {
                return ResponseEntity.badRequest().body("Meeting date cannot be empty");
            }

            Meeting savedMeetingDate = meetingDetailsService.saveMeetingDate(meetingId, meetingDate);
            if (savedMeetingDate == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to save meeting date");
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    //ZROBIONE
    // Endpoit do pobierania ludzi dla jakiesgos spotkania
    @GetMapping("/{meetingId}/participants")
    public ResponseEntity<?> getMeetingParticipants(
            @PathVariable Long meetingId,
            @RequestHeader(value = "Authorization") String authHeader) {

        String token = jwtTokenUtil.removeBearerPrefix(authHeader);

        if (jwtTokenUtil.isGuest(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Guest users cannot access this endpoint.");
        }

        try {
            MeetingParticipantsDTO participantsDTO = meetingService.getParticipants(meetingId);
            return ResponseEntity.ok(participantsDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found");
        }
    }

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
}
