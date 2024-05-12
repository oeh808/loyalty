package io.brightskies.loyalty.user.repository;

import io.brightskies.loyalty.user.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByEmail(String email);
    boolean existsUserByEmail(String email);
}

