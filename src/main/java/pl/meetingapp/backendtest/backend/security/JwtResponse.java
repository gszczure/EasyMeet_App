package pl.meetingapp.backendtest.backend.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String tokenType = "Bearer";
    private String token;
    private Long userId;

    public JwtResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }
}
