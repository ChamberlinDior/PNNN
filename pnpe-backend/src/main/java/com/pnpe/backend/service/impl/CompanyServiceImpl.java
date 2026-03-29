package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.CompanyRequest;
import com.pnpe.backend.dto.CompanyResponse;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.Company;
import com.pnpe.backend.model.enums.InterviewStatus;
import com.pnpe.backend.repository.CompanyRepository;
import com.pnpe.backend.repository.EmploymentPlacementRepository;
import com.pnpe.backend.repository.InterviewRepository;
import com.pnpe.backend.service.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final InterviewRepository interviewRepository;
    private final EmploymentPlacementRepository employmentPlacementRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository,
                              InterviewRepository interviewRepository,
                              EmploymentPlacementRepository employmentPlacementRepository) {
        this.companyRepository = companyRepository;
        this.interviewRepository = interviewRepository;
        this.employmentPlacementRepository = employmentPlacementRepository;
    }

    @Override
    public CompanyResponse create(CompanyRequest request) {
        Company company = new Company();
        company.setName(request.name());
        company.setSector(request.sector());
        company.setCity(request.city());
        company.setAddress(request.address());
        company.setContactName(request.contactName());
        company.setContactEmail(request.contactEmail());
        company.setContactPhone(request.contactPhone());
        company.setPartnershipNotes(request.partnershipNotes());
        if (request.status() != null) {
            company.setStatus(request.status());
        }
        return toResponse(companyRepository.save(company));
    }

    @Override
    public List<CompanyResponse> findAll() {
        return companyRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public CompanyResponse findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable"));
        return toResponse(company);
    }

    private CompanyResponse toResponse(Company company) {
        long totalInterviews = interviewRepository.findByCompanyIdOrderByInterviewDateDesc(company.getId()).size();
        long totalHires = employmentPlacementRepository.countByCompanyId(company.getId());
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getSector(),
                company.getCity(),
                company.getAddress(),
                company.getContactName(),
                company.getContactEmail(),
                company.getContactPhone(),
                company.getPartnershipNotes(),
                company.getStatus(),
                totalInterviews,
                totalHires
        );
    }
}
