package com.pnpe.backend.repository;

import com.pnpe.backend.model.EmploymentPlacement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmploymentPlacementRepository extends JpaRepository<EmploymentPlacement, Long> {
    List<EmploymentPlacement> findByCompanyId(Long companyId);
    List<EmploymentPlacement> findByCounselorId(Long counselorId);
    long countByCompanyId(Long companyId);
}
