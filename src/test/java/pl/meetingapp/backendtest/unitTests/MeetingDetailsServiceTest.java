package pl.meetingapp.backendtest.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.DateRangeRepository;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
import pl.meetingapp.backendtest.backend.service.MeetingDetailsService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetingDetailsServiceTest {

    @Mock
    private DateRangeRepository dateRangeRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @InjectMocks
    private MeetingDetailsService meetingDetailsService;

    private List<DateRange> testDateRanges;
    private Meeting testMeeting;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testMeeting = new Meeting("Test Meeting", testUser);
        testMeeting.setId(1L);

        DateRange dateRange1 = new DateRange();
        dateRange1.setId(1L);
        dateRange1.setMeeting(testMeeting);
        dateRange1.setUser(testUser);
        dateRange1.setStartDate(LocalDate.now());
        dateRange1.setStartTime("10:30");
        dateRange1.setDuration("2");

        DateRange dateRange2 = new DateRange();
        dateRange2.setId(2L);
        dateRange2.setMeeting(testMeeting);
        dateRange2.setUser(testUser);
        dateRange2.setStartDate(LocalDate.now().plusDays(1));
        dateRange2.setDuration("All Day");

        testDateRanges = Arrays.asList(dateRange1, dateRange2);
    }

    @Test
    void findDateRangesByMeetingId_Success() {
        // Arrange
        when(dateRangeRepository.findByMeetingId(anyLong())).thenReturn(testDateRanges);

        // Act
        List<DateRange> result = meetingDetailsService.findDateRangesByMeetingId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testDateRanges, result);
    }

    @Test
    void deleteAll_Success() {
        // Arrange
        doNothing().when(dateRangeRepository).deleteAll(anyList());

        // Act
        meetingDetailsService.deleteAll(testDateRanges);

        // Assert
        verify(dateRangeRepository, times(1)).deleteAll(testDateRanges);
    }

    @Test
    void saveMeetingDate_MeetingExists_Success() {
        // Arrange
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(testMeeting));
        when(meetingRepository.save(any(Meeting.class))).thenReturn(testMeeting);
        String newDate = "2023-06-15";

        // Act
        Meeting result = meetingDetailsService.saveMeetingDate(1L, newDate);

        // Assert
        assertNotNull(result);
        assertEquals(newDate, testMeeting.getMeetingDate());
        verify(meetingRepository).findById(1L);
        verify(meetingRepository).save(testMeeting);
    }

    @Test
    void saveMeetingDate_MeetingNotFound_ReturnsNull() {
        // Arrange
        when(meetingRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Meeting result = meetingDetailsService.saveMeetingDate(2L, "2023-06-15");

        // Assert
        assertNull(result);
        verify(meetingRepository).findById(2L);
        verify(meetingRepository, never()).save(any(Meeting.class));
    }

    @Test
    void findMeetingById_MeetingExists_Success() {
        // Arrange
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(testMeeting));

        // Act
        Optional<Meeting> result = meetingDetailsService.findMeetingById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testMeeting, result.get());
        verify(meetingRepository).findById(1L);
    }

    @Test
    void findMeetingById_MeetingNotFound_ReturnsEmptyOptional() {
        // Arrange
        when(meetingRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Optional<Meeting> result = meetingDetailsService.findMeetingById(2L);

        // Assert
        assertFalse(result.isPresent());
        verify(meetingRepository).findById(2L);
    }

    @Test
    void isUserOwner_UsernameMatchesOwner_ReturnsTrue() {
        // Arrange
        String username = "testuser";

        // Act
        boolean result = meetingDetailsService.isUserOwner(username, testMeeting);

        // Assert
        assertTrue(result);
    }

    @Test
    void isUserOwner_UsernameDoesNotMatchOwner_ReturnsFalse() {
        // Arrange
        String username = "differentuser";

        // Act
        boolean result = meetingDetailsService.isUserOwner(username, testMeeting);

        // Assert
        assertFalse(result);
    }

    @Test
    void isInvalidDate_DateIsNull_ReturnsTrue() {
        // Act
        boolean result = meetingDetailsService.isInvalidDate(null);

        // Assert
        assertTrue(result);
    }

    @Test
    void isInvalidDate_DateIsEmpty_ReturnsTrue() {
        // Act
        boolean result = meetingDetailsService.isInvalidDate("");

        // Assert
        assertTrue(result);
    }

    @Test
    void isInvalidDate_DateIsValid_ReturnsFalse() {
        // Act
        boolean result = meetingDetailsService.isInvalidDate("2023-05-15");

        // Assert
        assertFalse(result);
    }

}