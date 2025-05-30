package pl.meetingapp.backendtest.backend.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.meetingapp.backendtest.backend.dto.*;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.Selection;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.DateRangeRepository;
import pl.meetingapp.backendtest.backend.repository.DateSelectionRepository;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
import pl.meetingapp.backendtest.backend.repository.UserRepository;
import pl.meetingapp.backendtest.backend.service.MeetingDetailsService;
import pl.meetingapp.backendtest.backend.service.MeetingsService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetingsServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DateRangeRepository dateRangeRepository;

    @Mock
    private DateSelectionRepository dateSelectionRepository;

    @Mock
    private MeetingDetailsService meetingDetailsService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private MeetingsService meetingsService;

    private User testUser;
    private Meeting testMeeting;
    private List<DateRange> testDateRanges;
    private List<Selection> testSelections;
    private CreateMeetingRequestDTO createMeetingRequest;

    @BeforeEach
    void setUp() {
        // Test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Jan");
        testUser.setLastName("Kowalski");

        // Set up test meeting
        testMeeting = new Meeting("Test Meeting", testUser);
        testMeeting.setId(1L);
        testMeeting.setCode("ABC123");

        // Date ranges
        DateRange dateRange = new DateRange();
        dateRange.setId(1L);
        dateRange.setMeeting(testMeeting);
        dateRange.setUser(testUser);
        dateRange.setStartDate(LocalDate.now());
        dateRange.setStartTime("10:30");
        dateRange.setDuration("1");
        testDateRanges = Collections.singletonList(dateRange);

        // Selections (yes)
        Selection selection = new Selection(1L, 1L, 1L);
        selection.setState("yes");
        testSelections = Collections.singletonList(selection);

        // Create meeting request
        CreateDateRangeDTO dateRangeDTO = new CreateDateRangeDTO();
        dateRangeDTO.setStartDate(LocalDate.now());
        dateRangeDTO.setStartTime("10:30");
        dateRangeDTO.setDuration("1");

        createMeetingRequest = new CreateMeetingRequestDTO();
        createMeetingRequest.setName("New test Meeting");
        createMeetingRequest.setComment("Test comment");
        createMeetingRequest.setDateRanges(Collections.singletonList(dateRangeDTO));
    }

    @Test
    void createMeeting_Success() throws Exception {
        // Arrange
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
            when(meetingRepository.save(any(Meeting.class))).thenAnswer(invocation -> {
                Meeting meeting = invocation.getArgument(0);
                meeting.setId(1L);
                meeting.setCode("ABC123");
                return meeting;
            });
            when(dateRangeRepository.saveAll(anyList())).thenReturn(testDateRanges);

            // Act
            Meeting result = meetingsService.createMeeting(createMeetingRequest);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("New test Meeting", result.getName());
            assertEquals("ABC123", result.getCode());
            assertEquals(testUser.getId(), result.getOwner().getId());
            verify(meetingRepository, times(1)).save(any(Meeting.class));
            verify(dateRangeRepository, times(1)).saveAll(anyList());
        }
    }

    @Test
    void joinMeeting_Success_NewUser() {
        // Arrange
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setFirstName("Zbyszek");
        newUser.setLastName("Kowalski");

        when(meetingRepository.findByCode(anyString())).thenReturn(Optional.of(testMeeting));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(newUser));
        when(meetingRepository.save(any(Meeting.class))).thenReturn(testMeeting);

        // Act
        ResponseEntity<String> response = meetingsService.joinMeeting("ABC123", "newuser");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
    }

    @Test
    void joinMeeting_Conflict_UserIsOwner() {
        // Arrange
        when(meetingRepository.findByCode(anyString())).thenReturn(Optional.of(testMeeting));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<String> response = meetingsService.joinMeeting("ABC123", "testuser");

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(meetingRepository, never()).save(any(Meeting.class));
    }

    @Test
    void joinMeeting_Conflict_UserAlreadyParticipant() {
        // Arrange
        User participantUser = new User();
        participantUser.setId(3L);
        participantUser.setUsername("participant");
        participantUser.setFirstName("JanParticipant");
        participantUser.setLastName("Adamczyk");

        testMeeting.addParticipant(participantUser);

        when(meetingRepository.findByCode(anyString())).thenReturn(Optional.of(testMeeting));
        when(userRepository.findByUsername("participant")).thenReturn(Optional.of(participantUser));

        // Act
        ResponseEntity<String> response = meetingsService.joinMeeting("ABC123", "participant");

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(meetingRepository, never()).save(any(Meeting.class));
    }

    @Test
    void getMeetingsForUser_Success() {
        // Arrange
        when(meetingRepository.findByOwnerOrParticipantsContaining(any(User.class), any(User.class)))
                .thenReturn(Collections.singletonList(testMeeting));

        // Act
        List<MeetingDTO> result = meetingsService.getMeetingsForUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMeeting.getId(), result.get(0).getId());
        assertEquals(testMeeting.getName(), result.get(0).getName());
        assertEquals(testMeeting.getCode(), result.get(0).getCode());
    }

    @Test
    void findById_Success() {
        // Arrange
        when(meetingRepository.findById(anyLong())).thenReturn(Optional.of(testMeeting));

        // Act
        Meeting result = meetingsService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testMeeting.getId(), result.getId());
        assertEquals(testMeeting.getName(), result.getName());
    }

    @Test
    void getParticipants_Success() {
        // Arrange
        User participant = new User();
        participant.setId(2L);
        participant.setFirstName("Jane");
        participant.setLastName("Doe");
        testMeeting.addParticipant(participant);

        when(meetingRepository.findById(anyLong())).thenReturn(Optional.of(testMeeting));

        // Act
        MeetingParticipantsDTO result = meetingsService.getParticipants(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getOwner().getId());
        assertEquals(1, result.getParticipants().size());
        assertEquals(participant.getId(), result.getParticipants().get(0).getId());
    }

    @Test
    void removeUserFromMeeting_Success() {
        // Arrange
        User participant = new User();
        participant.setId(2L);
        participant.setFirstName("Jane");
        participant.setLastName("Doe");
        testMeeting.addParticipant(participant);

        when(meetingRepository.findById(anyLong())).thenReturn(Optional.of(testMeeting));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(participant));
        when(dateRangeRepository.findByMeetingIdAndUserId(anyLong(), anyLong())).thenReturn(testDateRanges);
        when(dateSelectionRepository.findByMeetingIdAndUserId(anyLong(), anyLong())).thenReturn(testSelections);
        doNothing().when(dateRangeRepository).deleteAll(anyList());
        doNothing().when(dateSelectionRepository).deleteAll(anyList());
        when(meetingRepository.save(any(Meeting.class))).thenReturn(testMeeting);

        // Act
        boolean result = meetingsService.removeUserFromMeeting(1L, 2L);

        // Assert
        assertTrue(result);
        verify(meetingRepository, times(1)).save(any(Meeting.class));
        verify(dateRangeRepository, times(1)).deleteAll(anyList());
        verify(dateSelectionRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void deleteMeeting_Success() {
        // Arrange
        when(meetingRepository.findById(anyLong())).thenReturn(Optional.of(testMeeting));
        when(dateSelectionRepository.findByMeetingId(anyLong())).thenReturn(testSelections);
        when(meetingDetailsService.findDateRangesByMeetingId(anyLong())).thenReturn(testDateRanges);
        doNothing().when(dateSelectionRepository).deleteAll(anyList());
        doNothing().when(meetingDetailsService).deleteAll(anyList());
        doNothing().when(meetingRepository).delete(any(Meeting.class));

        // Act
        meetingsService.deleteMeeting(1L);

        // Assert
        verify(dateSelectionRepository, times(1)).deleteAll(anyList());
        verify(meetingDetailsService, times(1)).deleteAll(anyList());
        verify(meetingRepository, times(1)).delete(any(Meeting.class));
    }

    @Test
    void getMeetingByCode_Success() {
        // Arrange
        when(meetingRepository.findByCode(anyString())).thenReturn(Optional.of(testMeeting));

        // Act
        Optional<Meeting> result = meetingsService.getMeetingByCode("ABC123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testMeeting.getId(), result.get().getId());
        assertEquals(testMeeting.getName(), result.get().getName());
    }
}