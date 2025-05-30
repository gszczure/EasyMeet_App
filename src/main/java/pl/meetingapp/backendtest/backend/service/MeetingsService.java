package pl.meetingapp.backendtest.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.meetingapp.backendtest.backend.dto.CreateMeetingRequestDTO;
import pl.meetingapp.backendtest.backend.dto.MeetingDTO;
import pl.meetingapp.backendtest.backend.dto.MeetingParticipantsDTO;
import pl.meetingapp.backendtest.backend.dto.ParticipantDTO;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.Selection;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.DateRangeRepository;
import pl.meetingapp.backendtest.backend.repository.DateSelectionRepository;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
import pl.meetingapp.backendtest.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class MeetingsService {

    private final MeetingRepository meetingRepository;

    private final UserRepository userRepository;

    private final DateRangeRepository dateRangeRepository;

    private final DateSelectionRepository dateSelectionRepository;

    private final MeetingDetailsService dateRangeService;

    @Transactional
    public Meeting createMeeting(CreateMeetingRequestDTO createMeetingRequest) {
        // Pobranie użytkownika na podstawie kontekstu bezpieczeństwa
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tworzenie spotkania
        Meeting meeting = new Meeting(createMeetingRequest.getName(), owner);
        meetingRepository.save(meeting);

        // Dodanie komentarza (jeśli istnieje)
        if (createMeetingRequest.getComment() != null && !createMeetingRequest.getComment().isEmpty()) {
            meeting.setComment(createMeetingRequest.getComment().trim());
        }

        // Zapisywanie przedziałów czasowych
        if (createMeetingRequest.getDateRanges() != null && !createMeetingRequest.getDateRanges().isEmpty()) {
            List<DateRange> dateRanges = createMeetingRequest.getDateRanges().stream()
                    .map(dto -> {
                        DateRange dateRange = new DateRange();
                        dateRange.setMeeting(meeting);
                        dateRange.setUser(owner);
                        dateRange.setStartDate(dto.getStartDate());
                        dateRange.setStartTime(dto.getStartTime());
                        dateRange.setDuration(dto.getDuration());
                        return dateRange;
                    })
                    .collect(Collectors.toList());

            dateRangeRepository.saveAll(dateRanges);
        }
        return meeting;
    }


    public ResponseEntity<String> joinMeeting(String code, String username) {
        // Znajdź spotkanie na podstawie kodu
        Optional<Meeting> meetingOpt = meetingRepository.findByCode(code);
        Meeting meeting = meetingOpt.get();

        //Znajdz uztykownika na podstawie username
        Optional<User> userOpt = userRepository.findByUsername(username);
        User user = userOpt.get();

        if (meeting.getParticipants().contains(user) ||
                (meeting.getOwner() != null && meeting.getOwner().equals(user))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        meeting.addParticipant(user);
        meetingRepository.save(meeting);

        return ResponseEntity.ok().build();
    }


    public List<MeetingDTO> getMeetingsForUser(User user) {
        List<Meeting> meetings = meetingRepository.findByOwnerOrParticipantsContaining(user, user);

        // Tworzymi liste nowa, przechodzimy po kazdym spotkaniu gdzie uzytkownik jest wlasciceilem lub po prostu nalezy do spotkania
        List<MeetingDTO> meetingDTOs = new ArrayList<>();
        for (Meeting meeting : meetings) {
            // Mapujemy wlasciciela
            ParticipantDTO ownerDTO = new ParticipantDTO(
                    meeting.getOwner().getId(),
                    meeting.getOwner().getFirstName(),
                    meeting.getOwner().getLastName()
            );
            String code = meeting.getCode();

            // tworzeymy obiekt MeetingDTO zawierajacy informacje ktore nas interesuja
            MeetingDTO meetingDTO = new MeetingDTO(
                    meeting.getId(),
                    meeting.getName(),
                    code,
                    ownerDTO
            );

            // Dodajemy MeetingDTO do listy MeetingDTOs
            meetingDTOs.add(meetingDTO);
        }
        // Zwracamy liste
        return meetingDTOs;
    }


    public Meeting findById(Long id) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        return meeting.orElse(null);
    }

    public MeetingParticipantsDTO getParticipants(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting with id: " + meetingId + " not found"));

        // Mapowanie ownera
        ParticipantDTO ownerDTO = new ParticipantDTO(
                meeting.getOwner().getId(),
                meeting.getOwner().getFirstName(),
                meeting.getOwner().getLastName()
        );

        // Mapowanie uczestników
        List<ParticipantDTO> participantDTOs = meeting.getParticipants()
                .stream()
                .map(participant -> new ParticipantDTO(
                        participant.getId(),
                        participant.getFirstName(),
                        participant.getLastName()
                ))
                .collect(Collectors.toList());

        return new MeetingParticipantsDTO(ownerDTO, participantDTOs);
    }

    @Transactional
    public boolean removeUserFromMeeting(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (meeting != null && user != null) {
            boolean removed = meeting.getParticipants().removeIf(participant -> participant.getId().equals(userId));
            if (removed) {
                meetingRepository.save(meeting);

                List<DateRange> dateRanges = dateRangeRepository.findByMeetingIdAndUserId(meetingId, userId);
                dateRangeRepository.deleteAll(dateRanges);

                List<Selection> selections = dateSelectionRepository.findByMeetingIdAndUserId(meetingId, userId);
                dateSelectionRepository.deleteAll(selections);

                return true;
            }
        }
        return false;
    }


    public void deleteMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting with id: " + meetingId + " not found"));

        List<Selection> selections = dateSelectionRepository.findByMeetingId(meetingId);
        dateSelectionRepository.deleteAll(selections);

        List<DateRange> dateRanges = dateRangeService.findDateRangesByMeetingId(meetingId);
        dateRangeService.deleteAll(dateRanges);

        meetingRepository.delete(meeting);
    }

//    public void addOrUpdateComment(Long meetingId, String comment) throws Exception {
//        Meeting meeting = meetingRepository.findById(meetingId)
//                .orElseThrow(() -> new Exception("Meeting not found"));
//        meeting.setComment(comment);
//        meetingRepository.save(meeting);
//    }

    public Optional<Meeting> getMeetingByCode(String code) {
        return meetingRepository.findByCode(code);
    }
}
