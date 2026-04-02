package com.pnpe.backend.repository;

import com.pnpe.backend.model.PreRegistration;
import com.pnpe.backend.model.enums.PreRegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PreRegistrationRepository extends JpaRepository<PreRegistration, Long> {

    Optional<PreRegistration> findByRequestNumber(String requestNumber);

    List<PreRegistration> findByStatus(PreRegistrationStatus status);

    boolean existsByRequestNumber(String requestNumber);

    boolean existsByAppointmentAt(LocalDateTime appointmentAt);

    List<PreRegistration> findByAppointmentAtBetweenOrderByAppointmentAtAsc(
            LocalDateTime start,
            LocalDateTime end
    );

    @Query(
            value = """
                    SELECT COALESCE(MAX(CAST(SUBSTRING(request_number, 4) AS UNSIGNED)), 1000)
                    FROM pre_registrations
                    WHERE request_number IS NOT NULL
                      AND request_number LIKE 'PR-%'
                    """,
            nativeQuery = true
    )
    Long findMaxRequestNumberValue();
}