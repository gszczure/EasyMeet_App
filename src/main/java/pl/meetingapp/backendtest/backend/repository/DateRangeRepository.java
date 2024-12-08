package pl.meetingapp.backendtest.backend.repository;

import pl.meetingapp.backendtest.backend.model.DateRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.meetingapp.backendtest.backend.model.User;

import java.util.List;

@Repository
public interface DateRangeRepository extends JpaRepository<DateRange, Long> {
    List<DateRange> findByMeetingId(Long meetingId);
    List<DateRange> findByUserAndMeetingId(User user, Long meetingId);
    List<DateRange> findByUserId(Long userId);

}
