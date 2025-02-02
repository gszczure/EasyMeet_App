package pl.meetingapp.backendtest.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VoteInfo {

    private String firstName;
    private String lastName;
    private String state;
    private Long userId;
}
