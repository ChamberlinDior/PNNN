package com.pnpe.backend.service;

import com.pnpe.backend.dto.DocumentResponse;
import com.pnpe.backend.model.enums.DocumentOwnerType;
import com.pnpe.backend.model.enums.DocumentSide;
import com.pnpe.backend.model.enums.DocumentType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    DocumentResponse uploadForPreRegistration(
            Long preRegistrationId,
            MultipartFile file,
            DocumentType documentType,
            DocumentSide side,
            String label,
            String documentNumber
    );

    DocumentResponse uploadForJobSeeker(
            Long jobSeekerId,
            MultipartFile file,
            DocumentType documentType,
            DocumentSide side,
            String label,
            String documentNumber
    );

    List<DocumentResponse> list(DocumentOwnerType ownerType, Long ownerId);

    Resource loadAsResource(Long documentId);

    DocumentResponse getMetadata(Long documentId);
}