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
import pl.meetingapp.backendtest.backend.controller.DateSelectionController;
import pl.meetingapp.backendtest.backend.model.Selection;
import pl.meetingapp.backendtest.backend.service.DateSelectionService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DateSelectionController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class DateSelectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DateSelectionService selectionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long meetingId;
    private Long userId;
    private Long dateRangeId;
    private Selection selection;
    private Map<String, String> userSelections;
    private Map<Long, Map<String, Long>> voteCounts;

    @BeforeEach
    void setUp() {
        meetingId = 1L;
        userId = 2L;
        dateRangeId = 3L;

        selection = new Selection(meetingId, userId, dateRangeId);
        selection.setState("yes");

        userSelections = new HashMap<>();
        userSelections.put("3", "yes");
        userSelections.put("4", "if_needed");

        voteCounts = new HashMap<>();
        Map<String, Long> counts1 = new HashMap<>();
        counts1.put("yes", 3L);
        counts1.put("if_needed", 2L);
        voteCounts.put(3L, counts1);

        Map<String, Long> counts2 = new HashMap<>();
        counts2.put("yes", 1L);
        counts2.put("if_needed", 4L);
        voteCounts.put(4L, counts2);
    }

    @Test
    void getUserSelections_ShouldReturnUserSelections() throws Exception {
        // Arrange
        when(selectionService.getUserSelections(meetingId, userId)).thenReturn(userSelections);

        // Act & Assert
        mockMvc.perform(get("/api/date-selections/{meetingId}/{userId}/user_selections", meetingId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.3").value("yes"))
                .andExpect(jsonPath("$.4").value("if_needed"));

        verify(selectionService).getUserSelections(meetingId, userId);
    }

    @Test
    void updateUserSelection_ShouldUpdateSelection() throws Exception {
        // Arrange
        doNothing().when(selectionService).updateUserSelection(
                eq(meetingId), eq(userId), eq(dateRangeId), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/date-selections/{meetingId}/{userId}/{dateRangeId}/update_selection",
                        meetingId, userId, dateRangeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(selection)))
                .andExpect(status().isOk());

        verify(selectionService).updateUserSelection(meetingId, userId, dateRangeId, "yes");
    }

    @Test
    void getVoteCounts_ShouldReturnVoteCounts() throws Exception {
        // Arrange
        when(selectionService.getVoteCounts(meetingId)).thenReturn(voteCounts);

        // Act & Assert
        mockMvc.perform(get("/api/date-selections/{meetingId}/votes", meetingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.3.yes").value(3))
                .andExpect(jsonPath("$.3.if_needed").value(2))
                .andExpect(jsonPath("$.4.yes").value(1))
                .andExpect(jsonPath("$.4.if_needed").value(4));

        verify(selectionService).getVoteCounts(meetingId);
    }

    @Test
    void deleteUserSelection_ShouldDeleteSelection() throws Exception {
        // Arrange
        doNothing().when(selectionService).deleteUserSelection(meetingId, userId, dateRangeId);

        // Act & Assert
        mockMvc.perform(delete("/api/date-selections/{meetingId}/{userId}/{dateRangeId}/delete_selection",
                        meetingId, userId, dateRangeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(selectionService).deleteUserSelection(meetingId, userId, dateRangeId);
    }
}

