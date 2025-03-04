package pl.meetingapp.backendtest.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.dto.GuestLoginRequest;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.security.JwtResponse;
import pl.meetingapp.backendtest.backend.security.JwtTokenUtil;
import pl.meetingapp.backendtest.backend.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;


    //TODO Zrobic walidacje wieksza po stronie backendu (na przyklad wpisanie poprawnie tego samego hasla drugi raz)

    // Rejestracja
    @PostMapping("/register")
    public User register(@Valid @RequestBody User user) {
        return userService.registerUser(user);
    }

    // Logowanie dla prawdziwych uzytkowników
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenUtil.generateToken(user.getUsername());
        Long userId = userService.getUserIdByUsername(user.getUsername());

        return ResponseEntity.ok(new JwtResponse(token, userId));
    }

    // Pseudo logowanie dla gości
    @PostMapping("/guest-login")
    public ResponseEntity<?> guestLogin(@RequestBody GuestLoginRequest request) {
        User guest = new User();
        guest.setFirstName(request.getFirstName());
        guest.setLastName(request.getLastName());
        guest.setGuest(true);
        guest.setUsername("Guest_" + UUID.randomUUID().toString().substring(0, 8));
        guest.setPassword("1233455");
        guest.setEmail("guest@guest.com");
        guest.setPhoneNumber("123456789");

        userService.registerUser(guest);
        Long userId = userService.getUserIdByUsername(guest.getUsername());

        String token = jwtTokenUtil.generateToken(guest.getUsername());
        return ResponseEntity.ok(new JwtResponse(token, userId));
    }

}
