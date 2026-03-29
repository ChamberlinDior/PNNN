package com.pnpe.backend.repository;

import com.pnpe.backend.model.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    Optional<Agency> findByCode(String code);
}
