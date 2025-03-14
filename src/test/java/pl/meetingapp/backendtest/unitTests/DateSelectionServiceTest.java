package pl.meetingapp.backendtest.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.meetingapp.backendtest.backend.model.Selection;
import pl.meetingapp.backendtest.backend.repository.DateSelectionRepository;
import pl.meetingapp.backendtest.backend.service.DateSelectionService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DateSelectionServiceTest {

    @Mock
    private DateSelectionRepository selectionRepository;

    @InjectMocks
    private DateSelectionService dateSelectionService;

    private List<Selection> testSelections;
    private Selection testSelection;

    @BeforeEach
    void setUp() {
        testSelection = new Selection(1L, 1L, 1L);
        testSelection.setState("yes");

        Selection selection2 = new Selection(1L, 1L, 2L);
        selection2.setState("if_needed");

        testSelections = Arrays.asList(testSelection, selection2);
    }

    @Test
    void getUserSelections_Success() {
        // Arrange
        when(selectionRepository.findByMeetingIdAndUserId(anyLong(), anyLong()))
                .thenReturn(testSelections);

        // Act
        Map<String, String> selections = dateSelectionService.getUserSelections(1L, 1L);

        // Assert
        assertNotNull(selections);
        assertEquals(2, selections.size());
        assertEquals("yes", selections.get("1"));
        assertEquals("if_needed", selections.get("2"));
    }

    @Test
    void updateUserSelection_ExistingSelection() {
        // Arrange
        when(selectionRepository.findByMeetingIdAndUserIdAndDateRangeId(anyLong(), anyLong(), anyLong()))
                .thenReturn(Optional.of(testSelection));
        when(selectionRepository.save(any(Selection.class))).thenReturn(testSelection);

        // Act
        dateSelectionService.updateUserSelection(1L, 1L, 1L, "no");

        // Assert
        verify(selectionRepository, times(1)).save(any(Selection.class));
        assertEquals("no", testSelection.getState());
    }

    @Test
    void updateUserSelection_NewSelection() {
        // Arrange
        when(selectionRepository.findByMeetingIdAndUserIdAndDateRangeId(anyLong(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(selectionRepository.save(any(Selection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        dateSelectionService.updateUserSelection(1L, 1L, 3L, "yes");

        // Assert
        verify(selectionRepository, times(1)).save(any(Selection.class));
    }

    @Test
    void getVoteCounts_Success() {
        // Arrange
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1L, 2L, 1L});
        mockResults.add(new Object[]{2L, 1L, 3L});

        when(selectionRepository.countVotesByDateRange(anyLong())).thenReturn(mockResults);

        // Act
        Map<Long, Map<String, Long>> voteCounts = dateSelectionService.getVoteCounts(1L);

        // Assert
        assertNotNull(voteCounts);
        assertEquals(2, voteCounts.size());

        Map<String, Long> dateRange1Counts = voteCounts.get(1L);
        assertEquals(2L, dateRange1Counts.get("yes"));
        assertEquals(1L, dateRange1Counts.get("if_needed"));

        Map<String, Long> dateRange2Counts = voteCounts.get(2L);
        assertEquals(1L, dateRange2Counts.get("yes"));
        assertEquals(3L, dateRange2Counts.get("if_needed"));
    }

    @Test
    void deleteUserSelection_Success() {
        // Arrange
        doNothing().when(selectionRepository).deleteByMeetingIdAndUserIdAndDateRangeId(anyLong(), anyLong(), anyLong());

        // Act
        dateSelectionService.deleteUserSelection(1L, 1L, 1L);

        // Assert
        verify(selectionRepository, times(1)).deleteByMeetingIdAndUserIdAndDateRangeId(1L, 1L, 1L);
    }
}