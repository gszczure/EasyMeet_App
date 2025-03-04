package pl.meetingapp.backendtest.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestLoginRequest {
    private String firstName;
    private String lastName;
}
