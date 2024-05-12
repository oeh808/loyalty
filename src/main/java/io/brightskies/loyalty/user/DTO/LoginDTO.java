package io.brightskies.loyalty.user.DTO;

import io.brightskies.loyalty.user.Entities.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginDTO {
    public User user;
    public  String token;
}
