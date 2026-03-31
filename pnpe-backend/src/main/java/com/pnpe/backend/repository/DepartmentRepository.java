package com.pnpe.backend.repository;

import com.pnpe.backend.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByAgencyIdOrderByNameAsc(Long agencyId);

    Optional<Department> findByIdAndAgencyId(Long id, Long agencyId);

    long countByAgencyId(Long agencyId);
}