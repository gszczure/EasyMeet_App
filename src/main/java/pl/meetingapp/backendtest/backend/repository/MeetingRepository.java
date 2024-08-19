package pl.meetingapp.backendtest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Optional<Meeting> findByCode(String code);
    List<Meeting> findByOwner(User owner);
}
