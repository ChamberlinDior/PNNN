package com.pnpe.backend.service;

import com.pnpe.backend.dto.CompanyRequest;
import com.pnpe.backend.dto.CompanyResponse;

import java.util.List;

public interface CompanyService {
    CompanyResponse create(CompanyRequest request);
    List<CompanyResponse> findAll();
    CompanyResponse findById(Long id);
}
