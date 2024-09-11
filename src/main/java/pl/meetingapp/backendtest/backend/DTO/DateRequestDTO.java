package pl.meetingapp.backendtest.backend.DTO;

import jakarta.validation.constraints.NotBlank;

public class DateRequestDTO {

    @NotBlank
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
