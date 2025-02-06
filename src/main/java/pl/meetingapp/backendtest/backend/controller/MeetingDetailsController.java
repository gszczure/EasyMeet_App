package pl.meetingapp.backendtest.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.dto.MeetingDateRangeDTO;
import pl.meetingapp.backendtest.backend.dto.MeetingDetailsDTO;
import pl.meetingapp.backendtest.backend.dto.VoteInfo;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.service.MeetingDetailsService;
import pl.meetingapp.backendtest.backend.service.MeetingsService;
import pl.meetingapp.backendtest.backend.service.VoteService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meeting-details")
@RequiredArgsConstructor
public class MeetingDetailsController {

    //TODO ZMIENIC NAZWE POL
    private final MeetingDetailsService dateRangeService;

    private final MeetingsService meetingService;

    private final VoteService voteService;


//    //Endpoint do usuwania wybranych przedzialow dat (musi miec id przedzialu daty by je usunac)
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteDateRange(@PathVariable Long id) {
//        dateRangeService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }


    //ZROBIONE
    // Endpoit do pobierania wszystkich potrzebnych informacji po wejsciu na strone gdzie uzytkownicy wybieraja daty
    @GetMapping("/details/{code}")
    public ResponseEntity<MeetingDetailsDTO> getMeetingDetails(@PathVariable String code) {
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

        List<MeetingDateRangeDTO> dateRanges = dateRangeService.findByMeetingId(meeting.getId()).stream()
                .map(dateRange -> new MeetingDateRangeDTO(
                        dateRange.getId(),
                        dateRange.getStartDate(),
                        dateRange.getStartTime(),
                        dateRange.getDuration()
                ))
                .collect(Collectors.toList());

        detailsDTO.setDateRanges(dateRanges);

        return ResponseEntity.ok(detailsDTO);
    }

    @GetMapping("/getVotes/{dateRangeId}")
    public List<VoteInfo> getVotes(@PathVariable Long dateRangeId) {
        return voteService.getVotesForDateRange(dateRangeId);
    }
}
