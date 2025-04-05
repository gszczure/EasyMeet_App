package pl.meetingapp.backendtest.backend.unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.meetingapp.backendtest.backend.dto.VoteInfo;
import pl.meetingapp.backendtest.backend.repository.DateSelectionRepository;
import pl.meetingapp.backendtest.backend.service.VoteService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VoteServiceTest {

    @Mock
    private DateSelectionRepository selectionRepository;

    @InjectMocks
    private VoteService voteService;

    private List<VoteInfo> testVoteInfos;

    @BeforeEach
    void setUp() {
        VoteInfo voteInfo1 = new VoteInfo("Grzegorz", "Kowalski", "yes", 1L);
        VoteInfo voteInfo2 = new VoteInfo("Aneta", "Kocioł", "if_needed", 2L);

        testVoteInfos = Arrays.asList(voteInfo1, voteInfo2);
    }

    @Test
    void getVotesForDateRange_Success() {
        // Arrange
        when(selectionRepository.findVotesByDateRangeId(anyLong())).thenReturn(testVoteInfos);

        // Act
        List<VoteInfo> result = voteService.getVotesForDateRange(1L);

        // Assert
        assertEquals("Grzegorz", result.get(0).getFirstName());
        assertEquals("Kowalski", result.get(0).getLastName());
        assertEquals("yes", result.get(0).getState());
        assertEquals(1L, result.get(0).getUserId());

        assertEquals("Aneta", result.get(1).getFirstName());
        assertEquals("Kocioł", result.get(1).getLastName());
        assertEquals("if_needed", result.get(1).getState());
        assertEquals(2L, result.get(1).getUserId());

    }
}