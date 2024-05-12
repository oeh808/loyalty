package io.brightskies.loyalty.user.controller;

import io.brightskies.loyalty.user.Entities.Role;
import io.brightskies.loyalty.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/generator")
@RequiredArgsConstructor
@CrossOrigin(origins="*",allowedHeaders = "*")
public class Generator {
    @Autowired
    private final RoleRepository roleRepository;
    @PostMapping("/roles")
    public String createRoles() {
        roleRepository.save(new Role("ROLE_USER"));
        return "Roles created successfully";
    }
}
