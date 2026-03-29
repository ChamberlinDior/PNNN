package com.pnpe.backend.repository;

import com.pnpe.backend.model.AgentProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentProfileRepository extends JpaRepository<AgentProfile, Long> {

    @EntityGraph(attributePaths = {"user", "user.role", "user.agency"})
    Optional<AgentProfile> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "user.role", "user.agency"})
    List<AgentProfile> findByCounselorTrueOrderByUserLastNameAscUserFirstNameAsc();

    @EntityGraph(attributePaths = {"user", "user.role", "user.agency"})
    List<AgentProfile> findAll();
}