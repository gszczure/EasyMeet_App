package pl.meetingapp.backendtest.backend.service;

import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.DateRangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DateRangeService {

    @Autowired
    private DateRangeRepository dateRangeRepository;

    public List<DateRange> saveDateRanges(List<DateRange> dateRanges) {
        return dateRangeRepository.saveAll(dateRanges);
    }

    public List<DateRange> findByMeetingId(Long meetingId) {
        return dateRangeRepository.findByMeetingId(meetingId);
    }

    public void deleteById(Long id) {
        dateRangeRepository.deleteById(id);
    }

}
