package io.brightskies.loyalty.user.controller;


import io.brightskies.loyalty.user.Entities.User;
import io.brightskies.loyalty.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins="*",allowedHeaders = "*")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @CrossOrigin(origins="*",allowedHeaders = "*")

    public User getMyData(Authentication authentication){
        return ((User) authentication.getPrincipal());
    }

    @GetMapping("{id}")
    @CrossOrigin(origins="*",allowedHeaders = "*")

    public User getUserData(@PathVariable("id") Long id){
        return userService.findById(id);
    }






}
