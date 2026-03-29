package com.pnpe.backend.repository;

import com.pnpe.backend.model.CounselorAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CounselorActionRepository extends JpaRepository<CounselorAction, Long> {
    List<CounselorAction> findByJobSeekerIdOrderByActionDateDesc(Long jobSeekerId);
    List<CounselorAction> findByCounselorIdOrderByActionDateDesc(Long counselorId);
}
