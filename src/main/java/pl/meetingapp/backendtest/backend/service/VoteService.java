package pl.meetingapp.backendtest.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.meetingapp.backendtest.backend.dto.VoteInfo;
import pl.meetingapp.backendtest.backend.repository.DateSelectionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final DateSelectionRepository selectionRepository;

    public List<VoteInfo> getVotesForDateRange(Long dateRangeId) {
        return selectionRepository.findVotesByDateRangeId(dateRangeId);
    }
}
