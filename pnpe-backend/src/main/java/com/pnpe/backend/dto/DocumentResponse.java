package com.pnpe.backend.dto;

import com.pnpe.backend.model.enums.DocumentOwnerType;
import com.pnpe.backend.model.enums.DocumentSide;
import com.pnpe.backend.model.enums.DocumentType;

public record DocumentResponse(
        Long id,
        DocumentOwnerType ownerType,
        DocumentType documentType,
        DocumentSide documentSide,
        String originalFilename,
        String label,
        String documentNumber,
        Boolean verified,
        String mimeType,
        String extension,
        Long sizeInBytes,
        Boolean previewable,
        String previewUrl,
        String downloadUrl
) {}