package pl.meetingapp.backendtest.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.meetingapp.backendtest.backend.model.Selection;
import pl.meetingapp.backendtest.backend.repository.SelectionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelectionService {

    private final SelectionRepository selectionRepository;

    public Map<String, String> getUserSelections(Long meetingId, Long userId) {
        List<Selection> selections = selectionRepository.findByMeetingIdAndUserId(meetingId, userId);
        return selections.stream()
                .collect(Collectors.toMap(
                        selection -> selection.getDateRangeId().toString(),
                        Selection::getState
                ));
    }

    @Transactional
    public void updateUserSelection(Long meetingId, Long userId, Long dateRangeId, String state) {
        Selection selection = selectionRepository.findByMeetingIdAndUserIdAndDateRangeId(meetingId, userId, dateRangeId)
                .orElse(new Selection(meetingId, userId, dateRangeId));
        selection.setState(state);
        selectionRepository.save(selection);
    }

    public Map<Long, Map<String, Long>> getVoteCounts(Long meetingId) {
        List<Object[]> results = selectionRepository.countVotesByDateRange(meetingId);
        Map<Long, Map<String, Long>> voteCounts = new HashMap<>();

        for (Object[] result : results) {
            Long dateRangeId = (Long) result[0];
            Long yesCount = (Long) result[1];
            Long ifNeededCount = (Long) result[2];

            Map<String, Long> counts = new HashMap<>();
            counts.put("yes", yesCount);
            counts.put("if_needed", ifNeededCount);

            voteCounts.put(dateRangeId, counts);
        }

        return voteCounts;
    }

    @Transactional
    public void deleteUserSelection(Long meetingId, Long userId, Long dateRangeId) {
        selectionRepository.deleteByMeetingIdAndUserIdAndDateRangeId(meetingId, userId, dateRangeId);
    }
}

