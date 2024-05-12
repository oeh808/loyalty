package io.brightskies.loyalty.user.controller;

import io.brightskies.loyalty.config.JwtUtil;
import io.brightskies.loyalty.user.DTO.LoginDTO;
import io.brightskies.loyalty.user.DTO.UserDTO;
import io.brightskies.loyalty.user.Entities.User;
import io.brightskies.loyalty.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins="*",allowedHeaders = "*")
public class AuthenticationController {
    @Autowired

    private final AuthenticationManager authenticationManager;
    @Autowired

    private final JwtUtil jwtUtil;
    @Autowired
    private final UserService userService;


    @PostMapping("/signup")
    public User createUser(@RequestBody UserDTO request) {
        return userService.addUser(request);
    }



    @PostMapping("/login")
    public LoginDTO login(@RequestBody UserDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        //
        final User user = userService.findByEmail(request.getEmail());

        System.out.println(user.toString());
        String token = jwtUtil.generateToken(user);

        return new LoginDTO(user, token);
    }

}

