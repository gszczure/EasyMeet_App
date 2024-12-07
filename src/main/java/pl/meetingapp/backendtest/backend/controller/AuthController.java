package pl.meetingapp.backendtest.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.security.JwtResponse;
import pl.meetingapp.backendtest.backend.security.JwtTokenUtil;
import pl.meetingapp.backendtest.backend.service.UserService;

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
    @PostMapping("/register")
    public User register(@Valid @RequestBody User user) {
        return userService.registerUser(user);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenUtil.generateToken(user.getUsername());
        Long userId = userService.getUserIdByUsername(user.getUsername());

        return ResponseEntity.ok(new JwtResponse(token, userId));
    }
}
