package io.brightskies.loyalty.user.controller;

import io.brightskies.loyalty.user.Entities.Role;
import io.brightskies.loyalty.user.repository.RoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("api/v1/generator")
@Tag(name = "Generator", description = "Controller for creating user roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Generator {
    @Autowired
    private final RoleRepository roleRepository;

    @Operation(description = "POST endpoint for creatin user roles" +
            "\n\n Returns a string confirming roles were generated successfully.", summary = "Create Roles")
    @PostMapping("/roles")
    public String createRoles() {
        log.info("Recieved: POST request to /api/v1/generator");
        roleRepository.save(new Role("ROLE_USER"));
        return "Roles created successfully";
    }
}
