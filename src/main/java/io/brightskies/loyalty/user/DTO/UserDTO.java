package io.brightskies.loyalty.user.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDTO {
    private String name;
    private String email ;
    private String password;
}
