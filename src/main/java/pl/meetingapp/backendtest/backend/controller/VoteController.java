package pl.meetingapp.backendtest.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.meetingapp.backendtest.backend.dto.VoteInfo;
import pl.meetingapp.backendtest.backend.service.VoteService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/api/getVotes/{dateRangeId}")
    public List<VoteInfo> getVotes(@PathVariable Long dateRangeId) {
        return voteService.getVotesForDateRange(dateRangeId);
    }
}
