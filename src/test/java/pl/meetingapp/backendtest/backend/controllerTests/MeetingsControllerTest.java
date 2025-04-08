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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import pl.meetingapp.backendtest.backend.config.TestSecurityConfig;
import pl.meetingapp.backendtest.backend.controller.MeetingsController;
import pl.meetingapp.backendtest.backend.dto.CreateDateRangeDTO;
import pl.meetingapp.backendtest.backend.dto.CreateMeetingRequestDTO;
import pl.meetingapp.backendtest.backend.dto.MeetingDTO;
import pl.meetingapp.backendtest.backend.dto.MeetingRequestDTO;
import pl.meetingapp.backendtest.backend.dto.ParticipantDTO;
import pl.meetingapp.backendtest.backend.model.Meeting;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.repository.MeetingRepository;
import pl.meetingapp.backendtest.backend.security.JwtTokenUtil;
import pl.meetingapp.backendtest.backend.service.MeetingsService;
import pl.meetingapp.backendtest.backend.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MeetingsController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class MeetingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetingsService meetingService;

    @MockBean
    private UserService userService;

    @MockBean
    private MeetingRepository meetingRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateMeetingRequestDTO createMeetingRequest;
    private MeetingDTO meetingDTO;
    private MeetingRequestDTO meetingRequestDTO;
    private User testUser;
    private Meeting testMeeting;
    private List<MeetingDTO> userMeetings;
    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        // Set up test data
        createMeetingRequest = new CreateMeetingRequestDTO();
        createMeetingRequest.setName("Test Meeting");

        List<CreateDateRangeDTO> dateRanges = new ArrayList<>();
        CreateDateRangeDTO dateRange = new CreateDateRangeDTO();
        dateRange.setStartDate(LocalDate.from(LocalDateTime.now()));
        dateRanges.add(dateRange);
        createMeetingRequest.setDateRanges(dateRanges);

        ParticipantDTO ownerDTO = new ParticipantDTO(1L, "testuser", "Test User");
        meetingDTO = new MeetingDTO(1L, "Test Meeting", "ABC123", ownerDTO);

        meetingRequestDTO = new MeetingRequestDTO();
        meetingRequestDTO.setCode("ABC123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testMeeting = new Meeting();
        testMeeting.setId(1L);
        testMeeting.setName("Test Meeting");
        testMeeting.setCode("ABC123");

        userMeetings = Arrays.asList(meetingDTO);

        // Mock authentication
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createMeeting_ShouldReturnCreatedMeeting() throws Exception {
        // Arrange
        when(meetingService.createMeeting(any(CreateMeetingRequestDTO.class))).thenReturn(meetingDTO);

        // Act & Assert
        mockMvc.perform(post("/api/meetings/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMeetingRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Meeting"))
                .andExpect(jsonPath("$.code").value("ABC123"));

        verify(meetingService).createMeeting(any(CreateMeetingRequestDTO.class));
    }

    @Test
    void deleteMeeting_ShouldDeleteMeeting() throws Exception {
        // Arrange
        doNothing().when(meetingService).deleteMeeting(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/meetings/{meetingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(meetingService).deleteMeeting(1L);
    }

    @Test
    void joinMeeting_ShouldJoinUserToMeeting() throws Exception {
        // Arrange
        when(meetingService.joinMeeting(anyString(), anyString())).thenReturn(ResponseEntity.ok().build());

        // Act & Assert
        mockMvc.perform(post("/api/meetings/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetingRequestDTO)))
                .andExpect(status().isOk());

        verify(meetingService).joinMeeting(eq("ABC123"), eq("testuser"));
    }

    @Test
    void joinMeetingByCode_ShouldRedirectToDateChosePage_WhenMeetingExists() throws Exception {
        // Arrange
        when(meetingRepository.findByCode("ABC123")).thenReturn(Optional.of(testMeeting));

        // Act & Assert
        mockMvc.perform(get("/api/meetings/join/{code}", "ABC123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/html/date-chose.html?code=ABC123"));

        verify(meetingRepository).findByCode("ABC123");
    }

    @Test
    void joinMeetingByCode_ShouldReturnNotFound_WhenMeetingDoesNotExist() throws Exception {
        // Arrange
        when(meetingRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/meetings/join/{code}", "INVALID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(meetingRepository).findByCode("INVALID");
    }

    @Test
    void getMeetingsForUser_ShouldReturnUserMeetings_WhenUserIsAuthenticated() throws Exception {
        // Arrange
        String authHeader = "Bearer valid_token";
        when(jwtTokenUtil.removeBearerPrefix(authHeader)).thenReturn("valid_token");
        when(jwtTokenUtil.isGuest("valid_token")).thenReturn(false);
        when(jwtTokenUtil.extractUsername("valid_token")).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(meetingService.getMeetingsForUser(testUser)).thenReturn(userMeetings);

        // Act & Assert
        mockMvc.perform(get("/api/meetings/for-user")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Meeting"))
                .andExpect(jsonPath("$[0].code").value("ABC123"));

        verify(jwtTokenUtil).removeBearerPrefix(authHeader);
        verify(jwtTokenUtil).isGuest("valid_token");
        verify(jwtTokenUtil).extractUsername("valid_token");
        verify(userService).findByUsername("testuser");
        verify(meetingService).getMeetingsForUser(testUser);
    }

    @Test
    void getMeetingsForUser_ShouldReturnForbidden_WhenUserIsGuest() throws Exception {
        // Arrange
        String authHeader = "Bearer guest_token";
        when(jwtTokenUtil.removeBearerPrefix(authHeader)).thenReturn("guest_token");
        when(jwtTokenUtil.isGuest("guest_token")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/meetings/for-user")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Guest users cannot access this endpoint."));

        verify(jwtTokenUtil).removeBearerPrefix(authHeader);
        verify(jwtTokenUtil).isGuest("guest_token");
        verify(jwtTokenUtil, never()).extractUsername(anyString());
        verify(userService, never()).findByUsername(anyString());
        verify(meetingService, never()).getMeetingsForUser(any(User.class));
    }
}
