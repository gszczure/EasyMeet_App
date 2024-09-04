package pl.meetingapp.backendtest.backend.security;

public class JwtResponse {
    private String tokenType = "Bearer";
    private String token;
    private Long userId;

    public JwtResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }
}
