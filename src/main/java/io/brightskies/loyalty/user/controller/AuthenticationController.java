package io.brightskies.loyalty.user.controller;

import io.brightskies.loyalty.config.JwtUtil;
import io.brightskies.loyalty.user.DTO.LoginDTO;
import io.brightskies.loyalty.user.DTO.UserDTO;
import io.brightskies.loyalty.user.Entities.User;
import io.brightskies.loyalty.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@Tag(name = "Authentication", description = "Controller for handling mappings for user authentication")
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {
    @Autowired

    private final AuthenticationManager authenticationManager;
    @Autowired

    private final JwtUtil jwtUtil;
    @Autowired
    private final UserService userService;

    @Operation(description = "POST endpoint for signing up a user" +
            "\n\n Returns the user created.", summary = "Create a user")
    @PostMapping("/signup")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Must conform to required properties of UserDTO")
    public User createUser(@Valid @RequestBody UserDTO request) {
        log.info("Recieved: POST request to /api/v1/auth/signup");
        return userService.addUser(request);
    }

    @Operation(description = "POST endpoint for logging in a user" +
            "\n\n Returns an instance of LoginDTO which contains the user and a JWT token.", summary = "Login a user")
    @PostMapping("/login")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Must conform to required properties of UserDTO")
    public LoginDTO login(@Valid @RequestBody UserDTO request) {
        log.info("Recieved: POST request to /api/v1/auth/login");

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final User user = userService.findByEmail(request.getEmail());

        log.info("Logged in user: " + user.toString());
        String token = jwtUtil.generateToken(user);

        return new LoginDTO(user, token);
    }

}
