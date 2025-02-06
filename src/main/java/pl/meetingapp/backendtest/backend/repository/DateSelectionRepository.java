package pl.meetingapp.backendtest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.meetingapp.backendtest.backend.dto.VoteInfo;
import pl.meetingapp.backendtest.backend.model.Selection;

import java.util.List;
import java.util.Optional;

@Repository
public interface DateSelectionRepository extends JpaRepository<Selection, Long> {
    List<Selection> findByMeetingIdAndUserId(Long meetingId, Long userId);
    Optional<Selection> findByMeetingIdAndUserIdAndDateRangeId(Long meetingId, Long userId, Long dateRangeId);

    @Query("SELECT s.dateRangeId, " +
            "SUM(CASE WHEN s.state = 'yes' THEN 1 ELSE 0 END) as yesCount, " +
            "SUM(CASE WHEN s.state = 'if_needed' THEN 1 ELSE 0 END) as ifNeededCount " +
            "FROM Selection s " +
            "WHERE s.meetingId = :meetingId " +
            "GROUP BY s.dateRangeId")
    List<Object[]> countVotesByDateRange(Long meetingId);

    @Query("SELECT new pl.meetingapp.backendtest.backend.dto.VoteInfo(u.firstName, u.lastName, s.state, s.userId) " +
            "FROM Selection s " +
            "JOIN User u ON u.id = s.userId " +
            "WHERE s.dateRangeId = :dateRangeId")
    List<VoteInfo> findVotesByDateRangeId(@Param("dateRangeId") Long dateRangeId);

    void deleteByMeetingIdAndUserIdAndDateRangeId(Long meetingId, Long userId, Long dateRangeId);

}

