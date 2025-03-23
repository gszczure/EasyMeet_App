package pl.meetingapp.backendtest.backend.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String tokenType = "Bearer";
    private String token;
    private Long userId;
    private boolean isGuest;


    public JwtResponse(String token, Long userId, boolean isGuest) {
        this.token = token;
        this.userId = userId;
        this.isGuest = isGuest;
    }
}
