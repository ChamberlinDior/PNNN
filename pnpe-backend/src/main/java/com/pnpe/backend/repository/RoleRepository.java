package com.pnpe.backend.repository;

import com.pnpe.backend.model.Role;
import com.pnpe.backend.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
