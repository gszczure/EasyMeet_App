package pl.meetingapp.backendtest.backend.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import pl.meetingapp.backendtest.backend.model.User;
import pl.meetingapp.backendtest.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@Valid @RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) { // dla logowania nie bedzie @Valid poniewaz musial bym stworzyc osobne klasy modelu user userregister i userlogin i osbno walidacje zrobic, lub usunac validacje z pola username i password w klasie User. Validacja ta bedzie tylko frontendowa
        return userService.authenticateUser(user.getUsername(), user.getPassword());
    }
}

