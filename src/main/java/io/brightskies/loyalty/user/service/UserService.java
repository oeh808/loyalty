package io.brightskies.loyalty.user.service;

import io.brightskies.loyalty.user.DTO.UserDTO;
import io.brightskies.loyalty.user.Entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByEmail(String email);

    User findById(Long id);

    User addUser(UserDTO user);

    void attachRoleToUser(String userName, String roleName);
}
