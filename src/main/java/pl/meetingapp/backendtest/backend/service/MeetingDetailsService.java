package pl.meetingapp.backendtest.backend.service;

import lombok.RequiredArgsConstructor;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.repository.DateRangeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MeetingDetailsService {

    private final DateRangeRepository dateRangeRepository;

//    @Autowired
//    private DateSelectionRepository selectionRepository;

//    // Metoda do zapisywanai przedzialu daty
//    public List<DateRange> saveDateRanges(List<DateRange> dateRanges) {
//        return dateRangeRepository.saveAll(dateRanges);
//    }

    public List<DateRange> findByMeetingId(Long meetingId) {
        return dateRangeRepository.findByMeetingId(meetingId);
    }

//    // Metoda do usuwania przedzialu daty w common dates
//    public void deleteById(Long id) {
//        dateRangeRepository.deleteById(id);
//    }


//    // Metoda do znalezienia dat dla danego uzytkownika w danym spotkaniu
//    public List<DateRange> findByUserAndMeeting(User user, Long meetingId) {
//        return dateRangeRepository.findByUserAndMeetingId(user, meetingId);
//    }

    // Metoda do usuwania dat spotkania
    public void deleteAll(List<DateRange> dateRanges) {
        dateRangeRepository.deleteAll(dateRanges);
    }

}