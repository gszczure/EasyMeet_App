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
import pl.meetingapp.backendtest.backend.service.MeetingDetailsService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetingDetailsServiceTest {

    @Mock
    private DateRangeRepository dateRangeRepository;

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
    void findByMeetingId_Success() {
        // Arrange
        when(dateRangeRepository.findByMeetingId(anyLong())).thenReturn(testDateRanges);

        // Act
        List<DateRange> result = meetingDetailsService.findByMeetingId(1L);

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
}