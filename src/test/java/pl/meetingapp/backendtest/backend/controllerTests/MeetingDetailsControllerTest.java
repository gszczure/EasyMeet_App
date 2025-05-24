package pl.meetingapp.backendtest.backend.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.meetingapp.backendtest.backend.config.TestSecurityConfig;
import pl.meetingapp.backendtest.backend.controller.MeetingDetailsController;
import pl.meetingapp.backendtest.backend.model.DateRange;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.security.JwtTokenUtil;
import pl.meetingapp.backendtest.backend.service.MeetingDetailsService;
import pl.meetingapp.backendtest.backend.service.MeetingsService;
import pl.meetingapp.backendtest.backend.service.UserService;
import pl.meetingapp.backendtest.backend.service.VoteService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MeetingDetailsController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class MeetingDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeetingDetailsService meetingDetailsService;

    @MockBean
    private MeetingsService meetingService;

    @MockBean
    private VoteService voteService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private Meeting meeting;
    private User user;
    private DateRange dateRange;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setGuest(false);

        meeting = new Meeting();
        meeting.setId(1L);
        meeting.setName("Test Meeting");
        meeting.setComment("Some comment");
        meeting.setOwner(user);

        dateRange = new DateRange();
        dateRange.setId(1L);
        dateRange.setMeeting(meeting);
        dateRange.setUser(user);
        dateRange.setStartDate(LocalDate.of(2025, 4, 20));
        dateRange.setStartTime("10:00");
        dateRange.setDuration("60");
    }

    @Test
    void getMeetingDetails_ShouldReturnMeetingDetails() throws Exception {
        when(meetingService.getMeetingByCode("ABC123")).thenReturn(Optional.of(meeting));
        when(meetingDetailsService.findDateRangesByMeetingId(1L)).thenReturn(List.of(dateRange));
        when(jwtTokenUtil.extractUsername("valid_token")).thenReturn("johndoe");
        when(userService.findByUsername("johndoe")).thenReturn(user);

        mockMvc.perform(get("/api/meeting-details/details/ABC123")
                        .header("Authorization", "Bearer valid_token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.meetingId").value(1))
                .andExpect(jsonPath("$.name").value("Test Meeting"))
                .andExpect(jsonPath("$.owner").value("John Doe"))
                .andExpect(jsonPath("$.ownerId").value(1))
                .andExpect(jsonPath("$.comment").value("Some comment"))
                .andExpect(jsonPath("$.dateRanges[0].id").value(1))
                .andExpect(jsonPath("$.guest").value(false));

    }

    @Test
    void getVotes_ShouldReturnVoteList_WhenUserNotGuest() throws Exception {
        when(jwtTokenUtil.removeBearerPrefix("Bearer valid_token")).thenReturn("valid_token");
        when(jwtTokenUtil.isGuest("valid_token")).thenReturn(false);
        when(voteService.getVotesForDateRange(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/meeting-details/getVotes/1")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk());
    }

    @Test
    void getVotes_ShouldReturnForbidden_WhenUserIsGuest() throws Exception {
        when(jwtTokenUtil.removeBearerPrefix("Bearer guest_token")).thenReturn("guest_token");
        when(jwtTokenUtil.isGuest("guest_token")).thenReturn(true);

        mockMvc.perform(get("/api/meeting-details/getVotes/1")
                        .header("Authorization", "Bearer guest_token"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Guest users cannot access vote data."));
    }

    @Test
    void saveMeetingDate_ShouldReturnOk_WhenOwnerAndDateValid() throws Exception {
        when(jwtTokenUtil.removeBearerPrefix("Bearer valid_token")).thenReturn("valid_token");
        when(jwtTokenUtil.extractUsername("valid_token")).thenReturn("johndoe");
        when(meetingDetailsService.findMeetingById(1L)).thenReturn(Optional.of(meeting));
        when(meetingDetailsService.isUserOwner("johndoe", meeting)).thenReturn(true);
        when(meetingDetailsService.isInvalidDate("2025-04-20")).thenReturn(false);
        when(meetingDetailsService.saveMeetingDate(1L, "2025-04-20")).thenReturn(meeting);

        mockMvc.perform(post("/api/meeting-details/1/save-date")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("meetingDate", "2025-04-20"))))
                .andExpect(status().isOk());
    }

    @Test
    void getParticipants_ShouldReturnForbidden_WhenGuest() throws Exception {
        when(jwtTokenUtil.removeBearerPrefix("Bearer guest_token")).thenReturn("guest_token");
        when(jwtTokenUtil.isGuest("guest_token")).thenReturn(true);

        mockMvc.perform(get("/api/meeting-details/1/participants")
                        .header("Authorization", "Bearer guest_token"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Guest users cannot access this endpoint."));
    }

    //TODO dodac wiecej testow np. dla removeParticipant
}
