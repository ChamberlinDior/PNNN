package com.pnpe.backend.repository;

import com.pnpe.backend.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"role", "agency", "department"})
    Optional<User> findWithRoleByEmail(String email);
}