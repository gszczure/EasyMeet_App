package pl.meetingapp.backendtest.backend.repository;

import pl.meetingapp.backendtest.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
