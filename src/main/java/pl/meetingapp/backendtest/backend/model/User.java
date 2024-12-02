package pl.meetingapp.backendtest.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Column(unique = true)
    private String username;
    @NotBlank
    @Size(min = 6)
    private String password;
    @NotBlank
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;
    @NotBlank
    @Pattern(regexp = "\\d{9}")
    private String phoneNumber;

}
