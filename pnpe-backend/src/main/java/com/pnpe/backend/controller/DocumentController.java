package com.pnpe.backend.controller;

import com.pnpe.backend.dto.DocumentResponse;
import com.pnpe.backend.exception.ResourceNotFoundException;
import com.pnpe.backend.model.JobSeekerDocument;
import com.pnpe.backend.model.enums.DocumentOwnerType;
import com.pnpe.backend.model.enums.DocumentSide;
import com.pnpe.backend.model.enums.DocumentType;
import com.pnpe.backend.repository.JobSeekerDocumentRepository;
import com.pnpe.backend.service.DocumentService;
import com.pnpe.backend.service.impl.FileSystemStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;
    private final JobSeekerDocumentRepository documentRepository;
    private final FileSystemStorageService fileSystemStorageService;

    public DocumentController(DocumentService documentService,
                              JobSeekerDocumentRepository documentRepository,
                              FileSystemStorageService fileSystemStorageService) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @PostMapping(value = "/pre-registration/{preRegistrationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentResponse uploadPreRegistrationDocument(@PathVariable Long preRegistrationId,
                                                          @RequestParam("file") MultipartFile file,
                                                          @RequestParam DocumentType documentType,
                                                          @RequestParam(defaultValue = "NOT_APPLICABLE") DocumentSide side,
                                                          @RequestParam(required = false) String label,
                                                          @RequestParam(required = false) String documentNumber) {
        return documentService.uploadForPreRegistration(preRegistrationId, file, documentType, side, label, documentNumber);
    }

    @PostMapping(value = "/job-seeker/{jobSeekerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentResponse uploadJobSeekerDocument(@PathVariable Long jobSeekerId,
                                                    @RequestParam("file") MultipartFile file,
                                                    @RequestParam DocumentType documentType,
                                                    @RequestParam(defaultValue = "NOT_APPLICABLE") DocumentSide side,
                                                    @RequestParam(required = false) String label,
                                                    @RequestParam(required = false) String documentNumber) {
        return documentService.uploadForJobSeeker(jobSeekerId, file, documentType, side, label, documentNumber);
    }

    @GetMapping
    public List<DocumentResponse> list(@RequestParam DocumentOwnerType ownerType,
                                       @RequestParam Long ownerId) {
        return documentService.list(ownerType, ownerId);
    }

    @GetMapping("/{documentId}")
    public DocumentResponse getMetadata(@PathVariable Long documentId) {
        return documentService.getMetadata(documentId);
    }

    @GetMapping("/{documentId}/preview")
    public ResponseEntity<Resource> preview(@PathVariable Long documentId) throws IOException {
        JobSeekerDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable"));

        Resource resource = documentService.loadAsResource(documentId);
        MediaType mediaType = resolveMediaType(document);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(document.getOriginalFilename(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .contentType(mediaType)
                .contentLength(resolveContentLength(document, resource))
                .body(resource);
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long documentId) throws IOException {
        JobSeekerDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable"));

        Resource resource = documentService.loadAsResource(documentId);
        MediaType mediaType = resolveMediaType(document);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(document.getOriginalFilename(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .contentType(mediaType)
                .contentLength(resolveContentLength(document, resource))
                .body(resource);
    }

    private MediaType resolveMediaType(JobSeekerDocument document) {
        String mimeType = fileSystemStorageService.detectMimeType(
                document.getStoragePath(),
                document.getOriginalFilename(),
                document.getMimeType()
        );

        try {
            return MediaType.parseMediaType(mimeType);
        } catch (InvalidMediaTypeException ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private long resolveContentLength(JobSeekerDocument document, Resource resource) throws IOException {
        if (document.getSizeInBytes() != null && document.getSizeInBytes() > 0) {
            return document.getSizeInBytes();
        }
        return resource.contentLength();
    }
}