package io.brightskies.loyalty.user.repository;

import io.brightskies.loyalty.user.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role,Long> {

    Role findByName(String name);
}

