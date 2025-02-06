package pl.meetingapp.backendtest.backend.service;

import pl.meetingapp.backendtest.backend.dto.VoteInfo;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.DateRangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.meetingapp.backendtest.backend.repository.DateSelectionRepository;

import java.util.*;

@Service
public class MeetingDetailsService {

    @Autowired
    private DateRangeRepository dateRangeRepository;

    @Autowired
    private DateSelectionRepository selectionRepository;

    // Metoda do zapisywanai przedzialu daty
    public List<DateRange> saveDateRanges(List<DateRange> dateRanges) {
        return dateRangeRepository.saveAll(dateRanges);
    }

    public List<DateRange> findByMeetingId(Long meetingId) {
        return dateRangeRepository.findByMeetingId(meetingId);
    }

    // Metoda do usuwania przedzialu daty w common dates
    public void deleteById(Long id) {
        dateRangeRepository.deleteById(id);
    }


    // Metoda do znalezienia dat dla danego uzytkownika w danym spotkaniu
    public List<DateRange> findByUserAndMeeting(User user, Long meetingId) {
        return dateRangeRepository.findByUserAndMeetingId(user, meetingId);
    }

    // Metoda do usuwania dat spotkania
    public void deleteAll(List<DateRange> dateRanges) {
        dateRangeRepository.deleteAll(dateRanges);
    }

}