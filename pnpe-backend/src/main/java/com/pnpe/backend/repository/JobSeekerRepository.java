package com.pnpe.backend.repository;

import com.pnpe.backend.model.JobSeeker;
import com.pnpe.backend.model.enums.JobSeekerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {

    Optional<JobSeeker> findByDossierNumber(String dossierNumber);

    Optional<JobSeeker> findByOpenNumber(String openNumber);

    boolean existsByDossierNumber(String dossierNumber);

    boolean existsByOpenNumber(String openNumber);

    boolean existsByPreRegistrationId(Long preRegistrationId);

    List<JobSeeker> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrOpenNumberContainingIgnoreCase(
            String lastName,
            String firstName,
            String openNumber
    );

    long countByStatus(JobSeekerStatus status);

    long countByAgencyId(Long agencyId);

    long countByAssignedAgentId(Long assignedAgentId);

    List<JobSeeker> findByAssignedAgentId(Long assignedAgentId);

    @Query(
            value = """
                    SELECT COALESCE(MAX(CAST(SUBSTRING(dossier_number, 5) AS UNSIGNED)), 1000)
                    FROM job_seekers
                    WHERE dossier_number IS NOT NULL
                      AND dossier_number LIKE 'DEM-%'
                    """,
            nativeQuery = true
    )
    Long findMaxDossierNumberValue();

    @Query(
            value = """
                    SELECT COALESCE(MAX(CAST(SUBSTRING(open_number, 6) AS UNSIGNED)), 50000)
                    FROM job_seekers
                    WHERE open_number IS NOT NULL
                      AND open_number LIKE 'OPEN-%'
                    """,
            nativeQuery = true
    )
    Long findMaxOpenNumberValue();
}