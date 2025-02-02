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
    @Size(min = 3, max = 20,
            message = "Username must be between 3 and 20 characters long")
    @Pattern(regexp = "^[^\\s]+$",
            message = "Username cannot contain spaces")
    @Column(unique = true)
    private String username;
    @NotBlank
    @Size(min = 6,
            message = "Password must be v0at least 6 characters long")
    private String password;
    @NotBlank
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
            message = "Invalid email format"
    )
    private String email;
    @NotBlank
    @Pattern(regexp = "\\d{9}",
            message = "Phone number must consist of exactly 9 digits"
    )
    private String phoneNumber;

}
