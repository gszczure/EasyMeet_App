package pl.meetingapp.backendtest.backend.service;

import lombok.RequiredArgsConstructor;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.repository.DateRangeRepository;
import org.springframework.stereotype.Service;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;

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
}