package pl.meetingapp.backendtest.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.model.Selection;
import pl.meetingapp.backendtest.backend.service.DateSelectionService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/date-selections")
public class DateSelectionController {

    private final DateSelectionService selectionService;

    //ZROBIONE
    //Endpoit do pobierania jak glosowal uzytkownik
    @GetMapping("/{meetingId}/{userId}/user_selections")
    public ResponseEntity<Map<String, String>> getUserSelections(@PathVariable Long meetingId, @PathVariable Long userId) {
        Map<String, String> selections = selectionService.getUserSelections(meetingId, userId);
        return ResponseEntity.ok(selections);
    }

    //ZROBIONE
    //Endpoit do zmiany glosu uzytkownika
    @PostMapping("/{meetingId}/{userId}/{dateRangeId}/update_selection")
    public ResponseEntity<Void> updateUserSelection(
            @PathVariable Long meetingId,
            @PathVariable Long userId,
            @PathVariable Long dateRangeId,
            @RequestBody Selection selection) {
        selectionService.updateUserSelection(meetingId, userId, dateRangeId, selection.getState());
        return ResponseEntity.ok().build();
    }

    //ZROBIONE
    //Endpoit pobiera liste glosaw dla kazdej daty w jakims spotkaniu (YES, IF_NEEDED)
    @GetMapping("/{meetingId}/votes")
    public ResponseEntity<Map<Long, Map<String, Long>>> getVoteCounts(@PathVariable Long meetingId) {
        Map<Long, Map<String, Long>> voteCounts = selectionService.getVoteCounts(meetingId);
        return ResponseEntity.ok(voteCounts);
    }

    //ZROBIONE
    //Endpoit do usuwania wyboru uzytkownika (kiedy zaznaczy none w frontendzie podczas wyboru daty)
    @DeleteMapping("/{meetingId}/{userId}/{dateRangeId}/delete_selection")
    public ResponseEntity<Void> deleteUserSelection(
            @PathVariable Long meetingId,
            @PathVariable Long userId,
            @PathVariable Long dateRangeId) {
        selectionService.deleteUserSelection(meetingId, userId, dateRangeId);
        return ResponseEntity.ok().build();
    }

}

