package pl.meetingapp.backendtest.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class MeetingDTO {
    private Long id;
    private String name;
    private String code;
    private ParticipantDTO owner;
    private List<ParticipantDTO> participants;
}

