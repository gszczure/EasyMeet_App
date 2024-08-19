package pl.meetingapp.backendtest.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.meetingapp.backendtest.backend.model.Meeting;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Optional<Meeting> findByCode(String code);
}
