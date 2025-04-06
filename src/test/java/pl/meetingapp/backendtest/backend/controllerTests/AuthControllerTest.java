package pl.meetingapp.backendtest.backend.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import pl.meetingapp.backendtest.backend.config.TestSecurityConfig;
import pl.meetingapp.backendtest.backend.controller.AuthController;
import pl.meetingapp.backendtest.backend.dto.GuestLoginRequest;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.security.JwtTokenUtil;
import pl.meetingapp.backendtest.backend.service.UserService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPhoneNumber("123456789");
        testUser.setGuest(false);

        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
    }

    @Test
    void register_ShouldRegisterUser() throws Exception {
        // Arrange
        when(userService.registerUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.guest").value(false));

        verify(userService).registerUser(any(User.class));
    }

    @Test
    void login_ShouldAuthenticateAndReturnToken() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenUtil.generateToken(eq("testuser"), eq(false))).thenReturn("test_token");
        when(userService.getUserIdByUsername("testuser")).thenReturn(1L);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test_token"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.guest").value(false));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil).generateToken("testuser", false);
        verify(userService).getUserIdByUsername("testuser");
    }

    @Test
    void guestLogin_ShouldRegisterGuestAndReturnToken() throws Exception {
        // Arrange
        GuestLoginRequest guestRequest = new GuestLoginRequest();
        guestRequest.setFirstName("Guest");
        guestRequest.setLastName("User");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        User guestUser = new User();
        guestUser.setFirstName("Guest");
        guestUser.setLastName("User");
        guestUser.setGuest(true);
        guestUser.setUsername("Guest_12345678");
        guestUser.setPassword("1233455");
        guestUser.setEmail("guest@guest.com");
        guestUser.setPhoneNumber("123456789");

        when(userService.registerUser(userCaptor.capture())).thenReturn(guestUser);
        // I use anyString() instead of a specific username since I can't predict the exact value becouse it's generated with UUID
        when(userService.getUserIdByUsername(anyString())).thenReturn(2L);
        when(jwtTokenUtil.generateToken(anyString(), eq(true))).thenReturn("guest_token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/guest-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("guest_token"))
                .andExpect(jsonPath("$.userId").value(2L))
                .andExpect(jsonPath("$.guest").value(true));

        User capturedUser = userCaptor.getValue();
        assertEquals("Guest", capturedUser.getFirstName());
        assertEquals("User", capturedUser.getLastName());
        assertTrue(capturedUser.isGuest());
        assertTrue(capturedUser.getUsername().startsWith("Guest_"));
        assertEquals("1233455", capturedUser.getPassword());
        assertEquals("guest@guest.com", capturedUser.getEmail());
        assertEquals("123456789", capturedUser.getPhoneNumber());

        verify(userService).registerUser(any(User.class));
        verify(userService).getUserIdByUsername(anyString());
        verify(jwtTokenUtil).generateToken(anyString(), eq(true));
    }
}

