package pl.meetingapp.backendtest.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.meetingapp.backendtest.backend.DTO.MeetingDTO;
import pl.meetingapp.backendtest.backend.DTO.ParticipantDTO;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.DTO.MeetingParticipantsDTO;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
import pl.meetingapp.backendtest.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DateRangeService dateRangeService;

    public Meeting createMeeting(String name, User owner) {
        Meeting meeting = new Meeting(name, owner);
        return meetingRepository.save(meeting);
    }

    public void saveMeetingDate(Long meetingId, String date) throws Exception {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new Exception("Meeting not found"));
        meeting.setMeetingDate(date);
        meetingRepository.save(meeting);
    }

    public ResponseEntity<String> joinMeeting(String code, String username) {
        // Znajdź spotkanie na podstawie kodu
        Optional<Meeting> meetingOpt = meetingRepository.findByCode(code);
        Meeting meeting = meetingOpt.get();

        //Znajdz uztykownika na podstawie username
        Optional<User> userOpt = userRepository.findByUsername(username);
        User user = userOpt.get();

        if (meeting.getParticipants().contains(user)) {
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
            // Sprawdzanie czy uzytkownik jest wlasciceilem jesli jest to w JSON Code: bedzie miala code napisany, ale jesli nie jest to Code: bedzie null
            String code = null;
            if (meeting.getOwner().equals(user)) {
                code = meeting.getCode();
            }

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
    public void removeUserFromMeeting(Long meetingId, String username) {
        Meeting meeting = findById(meetingId);
        if (meeting != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                meeting.getParticipants().remove(user);

                meetingRepository.save(meeting);
            }
        }
    }

    public MeetingParticipantsDTO getParticipants(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting with id: " + meetingId + " not found"));

        // Mapowanie ownera
        ParticipantDTO ownerDTO = new ParticipantDTO(
                meeting.getOwner().getId(),
                meeting.getOwner().getFirstName(),
                meeting.getOwner().getLastName(),
                meeting.getOwner().getUsername()
        );

        // Mapowanie uzytkowanikow
        List<ParticipantDTO> participantDTOs = meeting.getParticipants().stream()
                .map(participant -> new ParticipantDTO(
                        participant.getId(),
                        participant.getFirstName(),
                        participant.getLastName(),
                        participant.getUsername()
                ))
                .collect(Collectors.toList());

        // Dodajemy ownera do uzytkownikow tez
        participantDTOs.add(ownerDTO);

        // Zwracamy nowy MeetingParticipantsDTO
        return new MeetingParticipantsDTO(ownerDTO, participantDTOs);
    }


    public void deleteMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting with id: " + meetingId + " not found"));

        // pobieranie wszystkich DateRanges i usuwanie ich
        List<DateRange> dateRanges = dateRangeService.findByMeetingId(meetingId);
        dateRangeService.deleteAll(dateRanges);

        // Usuwanie spotkania
        meetingRepository.delete(meeting);
    }
    public void addOrUpdateComment(Long meetingId, String comment) throws Exception {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new Exception("Meeting not found"));
        meeting.setComment(comment);
        meetingRepository.save(meeting);
    }
}
