package io.brightskies.loyalty.user.service.impl;

import io.brightskies.loyalty.user.DTO.UserDTO;
import io.brightskies.loyalty.user.Entities.Role;
import io.brightskies.loyalty.user.Entities.User;
import io.brightskies.loyalty.user.exception.DuplicateResourceException;
import io.brightskies.loyalty.user.exception.ResourceNotFoundException;
import io.brightskies.loyalty.user.exception.UserExceptionMessages;
import io.brightskies.loyalty.user.repository.RoleRepository;
import io.brightskies.loyalty.user.repository.UserRepository;
import io.brightskies.loyalty.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(String email) {
        log.info("Running findByEmail(" + email + ") in UserServiceImpl...");
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email [%s] not found".formatted(email)));
    }

    @Override
    public User findById(Long id) {
        log.info("Running findById(" + id + ") in UserServiceImpl...");
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id [%s] not found".formatted(id)));
    }

    @Override
    public User addUser(UserDTO request) {
        log.info("Running addUser(" + request.toString() + ") in UserServiceImpl...");

        if (userRepository.existsUserByEmail(request.getEmail())) {
            log.error("Duplicate user email: " + request.getEmail() + "!");
            throw new DuplicateResourceException(UserExceptionMessages.DUPLICATE_EMAIL);
        }

        String pass = request.getPassword();
        String hashPass = passwordEncoder.encode(pass);
        request.setPassword(hashPass);
        User newuser = userRepository.save(new User(
                request.getName(),
                request.getEmail(),
                request.getPassword()));
        this.attachRoleToUser(request.getEmail(), "ROLE_USER");
        return newuser;
    }

    @Override
    public void attachRoleToUser(String email, String roleName) {
        log.info("Running attachRoleToUser(" + email + ", " + roleName + ") in UserServiceImpl...");
        log.info("Searching for role by name...");
        Role r = roleRepository.findByName(roleName);
        log.info("Searching for user by email...");
        User u = this.findByEmail(email);
        u.addRole(r);
        userRepository.save(u);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Running loadUserByUsername(" + email + ") in UserServiceImpl...");
        User user = this.findByEmail(email);
        if (user == null) {
            log.error("Incorrect email or password!");
            throw new UsernameNotFoundException(UserExceptionMessages.INVALID_EMAIL_OR_PASSWORD);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                user.mapRolesToAuthorities());
    }

}
