package pl.meetingapp.backendtest.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.DTO.MeetingDateRangeDTO;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.DTO.CreateDateRangeDTO;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.service.DateRangeService;
import pl.meetingapp.backendtest.backend.service.MeetingService;
import pl.meetingapp.backendtest.backend.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/date-ranges")
public class DateRangeController {

    @Autowired
    private DateRangeService dateRangeService;

    @Autowired
    private UserService userService;

    @Autowired
    private MeetingService meetingService;

    //ZROBIONE
    //Endpdoint do zapisywania dat w bazie danych
    @PostMapping
    public ResponseEntity<String> createDateRanges(@RequestBody List<CreateDateRangeDTO> dateRangesDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        // Pobieramy istniejące daty dla spotkania i użytkownika
        List<DateRange> existingDateRanges = dateRangeService.findByUserId(user.getId());

        // Przechodzimy przez nowe daty i sprawdzamy konflikty
        for (CreateDateRangeDTO dto : dateRangesDto) {
            Meeting meeting = meetingService.findById(dto.getMeetingId());

            // Sprawdzamy, czy istniejące daty tego samego użytkownika i spotkania kolidują
            boolean conflict = existingDateRanges.stream().anyMatch(existingRange -> existingRange.getMeeting().getId().equals(meeting.getId()) &&
                    existingRange.getUser().getId().equals(user.getId()) &&
                    (isDateRangeConflict(existingRange, dto)));

            if (conflict) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("The selected date range overlaps with an existing range. Please choose a different date range or ensure it doesn't match the same start and end dates as an already selected range.");
            }
        }

        // Mapowanie DTO na encje DateRange
        List<DateRange> dateRanges = dateRangesDto.stream().map(dto -> {
            DateRange dateRange = new DateRange();
            Meeting meeting = meetingService.findById(dto.getMeetingId());
            dateRange.setMeeting(meeting);
            dateRange.setUser(user);
            dateRange.setStartDate(dto.getStartDate());
            dateRange.setEndDate(dto.getEndDate());
            return dateRange;
        }).collect(Collectors.toList());

        dateRangeService.saveDateRanges(dateRanges);

        return ResponseEntity.ok().build();
    }

    // Funkcja sprawdzająca, czy dwa przedziały dat się nakładają
    private boolean isDateRangeConflict(DateRange existingRange, CreateDateRangeDTO newRange) {
        LocalDate existingStart = existingRange.getStartDate();
        LocalDate existingEnd = existingRange.getEndDate();
        LocalDate newStart = newRange.getStartDate();
        LocalDate newEnd = newRange.getEndDate();

        // Sprawdzamy, czy nowy przedział dat zachodzi na istniejący
        return (newStart.isBefore(existingEnd) || newStart.isEqual(existingEnd)) &&
                (newEnd.isAfter(existingStart) || newEnd.isEqual(existingStart));
    }

    //ZROBIONE
    //Endponit do pobierania dat dla danego spotkania
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<List<MeetingDateRangeDTO>> getDateRangesForMeeting(@PathVariable Long meetingId) {
        List<DateRange> dateRanges = dateRangeService.findByMeetingId(meetingId);

        List<MeetingDateRangeDTO> MeetingDateRangeDTOs = dateRanges.stream()
                .map(dateRange -> new MeetingDateRangeDTO(
                        dateRange.getId(),
                        dateRange.getStartDate(),
                        dateRange.getEndDate(),
                        dateRange.getUser().getFirstName() + " " + dateRange.getUser().getLastName(),
                        dateRange.getUser().getId()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(MeetingDateRangeDTOs);
    }

    //ZROBIONE
    //Endpoint do usuwania wybranych przedzialow dat (musi miec id przedzialu daty by je usunac)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDateRange(@PathVariable Long id) {
        dateRangeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //ZROBIONE
    //Endpoint do pobierania wspolnych dat
    @GetMapping("/meeting/{meetingId}/common-dates")
    public ResponseEntity<List<LocalDate>> getCommonDatesForMeeting(@PathVariable Long meetingId) {
        List<LocalDate> commonDates = dateRangeService.getCommonDatesForMeeting(meetingId);
        return ResponseEntity.ok(commonDates);
    }

}
