package pl.meetingapp.backendtest.backend.service;

import lombok.RequiredArgsConstructor;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.repository.DateRangeRepository;
import org.springframework.stereotype.Service;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MeetingDetailsService {

    private final DateRangeRepository dateRangeRepository;

    private final MeetingRepository meetingRepository;

    public List<DateRange> findDateRangesByMeetingId(Long meetingId) {
        return dateRangeRepository.findByMeetingId(meetingId);
    }

    // Metoda do usuwania dat spotkania
    public void deleteAll(List<DateRange> dateRanges) {
        dateRangeRepository.deleteAll(dateRanges);
    }

    public Meeting saveMeetingDate(Long meetingId, String meetingDate) {
        Optional<Meeting> meetingOptional = meetingRepository.findById(meetingId);
        if (meetingOptional.isPresent()) {
            Meeting meeting = meetingOptional.get();
            meeting.setMeetingDate(meetingDate);
            return meetingRepository.save(meeting);
        }
        return null;
    }

    public Optional<Meeting> findMeetingById(Long meetingId) {
        return meetingRepository.findById(meetingId);
    }

    public boolean isUserOwner(String username, Meeting meeting) {
        return username.equals(meeting.getOwner().getUsername());
    }

    public boolean isInvalidDate(String meetingDate) {
        return meetingDate == null || meetingDate.isEmpty();
    }

    //Metoda do oblicznia przedzialu daty np (19:00 + 3h duration = 19:00 - 22:00)
    public static String calculateTimeRange(LocalDate startDate, String startTime, String duration) {
        if (startDate == null || startTime == null || duration == null) return null;

        if (duration.equalsIgnoreCase("All Day")) {
            return "All Day";
        }

        try {
            String[] parts = startTime.split(":");
            int startHour = Integer.parseInt(parts[0]);
            int startMinute = Integer.parseInt(parts[1]);
            int durationHours = Integer.parseInt(duration);

            LocalTime time = LocalTime.of(startHour, startMinute);
            LocalDateTime startDateTime = LocalDateTime.of(startDate, time);
            LocalDateTime endDateTime = startDateTime.plusHours(durationHours);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return startDateTime.format(formatter) + " - " + endDateTime.format(formatter);
        } catch (Exception e) {
            return null;
        }
    }
}