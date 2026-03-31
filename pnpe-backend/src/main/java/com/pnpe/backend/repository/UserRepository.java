package com.pnpe.backend.repository;

import com.pnpe.backend.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByAgencyId(Long agencyId);

    @EntityGraph(attributePaths = {"role", "agency", "department"})
    Optional<User> findWithRoleByEmail(String email);

    @EntityGraph(attributePaths = {"role", "agency", "department"})
    Optional<User> findWithRoleByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"role", "agency", "department"})
    Optional<User> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"role", "agency", "department"})
    List<User> findByAgencyIdOrderByLastNameAscFirstNameAsc(Long agencyId);
}