package pl.meetingapp.backendtest.backend.security;

public class JwtResponse {
    private String tokenType = "Bearer";
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getToken() {
        return token;
    }
}