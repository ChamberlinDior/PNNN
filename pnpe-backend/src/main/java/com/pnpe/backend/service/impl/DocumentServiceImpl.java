package com.pnpe.backend.service.impl;

import com.pnpe.backend.dto.DocumentResponse;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.JobSeeker;
import com.pnpe.backend.model.JobSeekerDocument;
import com.pnpe.backend.model.PreRegistration;
import com.pnpe.backend.model.enums.DocumentOwnerType;
import com.pnpe.backend.model.enums.DocumentSide;
import com.pnpe.backend.model.enums.DocumentType;
import com.pnpe.backend.repository.JobSeekerDocumentRepository;
import com.pnpe.backend.repository.JobSeekerRepository;
import com.pnpe.backend.repository.PreRegistrationRepository;
import com.pnpe.backend.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final JobSeekerDocumentRepository documentRepository;
    private final PreRegistrationRepository preRegistrationRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final FileSystemStorageService fileStorageService;

    public DocumentServiceImpl(JobSeekerDocumentRepository documentRepository,
                               PreRegistrationRepository preRegistrationRepository,
                               JobSeekerRepository jobSeekerRepository,
                               FileSystemStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.preRegistrationRepository = preRegistrationRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public DocumentResponse uploadForPreRegistration(Long preRegistrationId,
                                                     MultipartFile file,
                                                     DocumentType documentType,
                                                     DocumentSide side,
                                                     String label,
                                                     String documentNumber) {
        PreRegistration preRegistration = preRegistrationRepository.findById(preRegistrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pré-inscription introuvable"));

        String storagePath = fileStorageService.store("pre-registrations/" + preRegistrationId, file);

        JobSeekerDocument document = new JobSeekerDocument();
        document.setOwnerType(DocumentOwnerType.PRE_REGISTRATION);
        document.setDocumentType(documentType);
        document.setDocumentSide(side);
        document.setOriginalFilename(file.getOriginalFilename());
        document.setStoragePath(storagePath);
        document.setMimeType(file.getContentType());
        document.setSizeInBytes(file.getSize());
        document.setLabel(label);
        document.setDocumentNumber(documentNumber);
        document.setScannedAt(LocalDateTime.now());
        document.setPreRegistration(preRegistration);

        JobSeekerDocument saved = documentRepository.save(document);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public DocumentResponse uploadForJobSeeker(Long jobSeekerId,
                                               MultipartFile file,
                                               DocumentType documentType,
                                               DocumentSide side,
                                               String label,
                                               String documentNumber) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Demandeur introuvable"));

        String storagePath = fileStorageService.store("job-seekers/" + jobSeekerId, file);

        JobSeekerDocument document = new JobSeekerDocument();
        document.setOwnerType(DocumentOwnerType.JOB_SEEKER);
        document.setDocumentType(documentType);
        document.setDocumentSide(side);
        document.setOriginalFilename(file.getOriginalFilename());
        document.setStoragePath(storagePath);
        document.setMimeType(file.getContentType());
        document.setSizeInBytes(file.getSize());
        document.setLabel(label);
        document.setDocumentNumber(documentNumber);
        document.setScannedAt(LocalDateTime.now());
        document.setJobSeeker(jobSeeker);

        JobSeekerDocument saved = documentRepository.save(document);

        if (documentType == DocumentType.CV) {
            jobSeeker.setMainCvUrl("/api/documents/" + saved.getId() + "/preview");
            jobSeeker.setLastCvUpdateAt(LocalDateTime.now());
            jobSeekerRepository.save(jobSeeker);
        }

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> list(DocumentOwnerType ownerType, Long ownerId) {
        return switch (ownerType) {
            case PRE_REGISTRATION -> documentRepository.findByPreRegistrationIdOrderByCreatedAtDesc(ownerId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
            case JOB_SEEKER -> documentRepository.findByJobSeekerIdOrderByCreatedAtDesc(ownerId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
            default -> List.of();
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadAsResource(Long documentId) {
        JobSeekerDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable"));
        return fileStorageService.load(document.getStoragePath());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getMetadata(Long documentId) {
        JobSeekerDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable"));
        return toResponse(document);
    }

    private DocumentResponse toResponse(JobSeekerDocument document) {
        String mimeType = fileStorageService.detectMimeType(
                document.getStoragePath(),
                document.getOriginalFilename(),
                document.getMimeType()
        );
        String extension = fileStorageService.getExtension(document.getOriginalFilename());
        boolean previewable = fileStorageService.isPreviewable(mimeType, document.getOriginalFilename());

        return new DocumentResponse(
                document.getId(),
                document.getOwnerType(),
                document.getDocumentType(),
                document.getDocumentSide(),
                document.getOriginalFilename(),
                document.getLabel(),
                document.getDocumentNumber(),
                document.getVerified(),
                mimeType,
                extension,
                document.getSizeInBytes(),
                previewable,
                "/api/documents/" + document.getId() + "/preview",
                "/api/documents/" + document.getId() + "/download"
        );
    }
}