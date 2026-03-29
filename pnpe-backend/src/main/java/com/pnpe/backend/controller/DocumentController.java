package com.pnpe.backend.controller;

import com.pnpe.backend.dto.DocumentResponse;
import com.pnpe.backend.model.enums.DocumentOwnerType;
import com.pnpe.backend.model.enums.DocumentSide;
import com.pnpe.backend.model.enums.DocumentType;
import com.pnpe.backend.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
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

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long documentId) {
        Resource resource = documentService.loadAsResource(documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}