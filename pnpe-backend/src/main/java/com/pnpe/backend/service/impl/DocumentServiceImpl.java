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
import java.util.ArrayList;
import java.util.Comparator;
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
        document.setDocumentSide(side != null ? side : DocumentSide.NOT_APPLICABLE);
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
        document.setDocumentSide(side != null ? side : DocumentSide.NOT_APPLICABLE);
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
        if (ownerType == null) {
            throw new IllegalArgumentException("ownerType est obligatoire");
        }

        if (ownerId == null) {
            throw new IllegalArgumentException("ownerId est obligatoire");
        }

        if (ownerType == DocumentOwnerType.PRE_REGISTRATION) {
            return listPreRegistrationDocuments(ownerId);
        }

        if (ownerType == DocumentOwnerType.JOB_SEEKER) {
            return listJobSeekerDocumentsIncludingPreRegistration(ownerId);
        }

        throw new IllegalArgumentException("Type de propriétaire non supporté : " + ownerType);
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

    private List<DocumentResponse> listPreRegistrationDocuments(Long preRegistrationId) {
        return documentRepository.findByPreRegistrationIdOrderByCreatedAtDesc(preRegistrationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private List<DocumentResponse> listJobSeekerDocumentsIncludingPreRegistration(Long jobSeekerId) {
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Demandeur introuvable"));

        List<JobSeekerDocument> merged = new ArrayList<>();

        List<JobSeekerDocument> jobSeekerDocuments =
                documentRepository.findByJobSeekerIdOrderByCreatedAtDesc(jobSeekerId);
        merged.addAll(jobSeekerDocuments);

        if (jobSeeker.getPreRegistration() != null && jobSeeker.getPreRegistration().getId() != null) {
            Long preRegistrationId = jobSeeker.getPreRegistration().getId();

            List<JobSeekerDocument> preRegistrationDocuments =
                    documentRepository.findByPreRegistrationIdOrderByCreatedAtDesc(preRegistrationId);

            merged.addAll(preRegistrationDocuments);
        }

        merged.sort(
                Comparator.comparing(JobSeekerDocument::getCreatedAt,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(JobSeekerDocument::getId,
                                Comparator.nullsLast(Comparator.reverseOrder()))
        );

        return merged.stream()
                .map(this::toResponse)
                .toList();
    }

    private DocumentResponse toResponse(JobSeekerDocument document) {
        String effectiveMimeType = fileStorageService.detectMimeType(
                document.getStoragePath(),
                document.getOriginalFilename(),
                document.getMimeType()
        );

        String extension = fileStorageService.getExtension(document.getOriginalFilename());
        boolean previewable = fileStorageService.isPreviewable(effectiveMimeType, document.getOriginalFilename());

        return new DocumentResponse(
                document.getId(),
                document.getOwnerType(),
                document.getDocumentType(),
                document.getDocumentSide(),
                document.getOriginalFilename(),
                document.getLabel(),
                document.getDocumentNumber(),
                Boolean.TRUE.equals(document.getVerified()),
                effectiveMimeType,
                extension,
                document.getSizeInBytes(),
                previewable,
                "/api/documents/" + document.getId() + "/preview",
                "/api/documents/" + document.getId() + "/download"
        );
    }
}