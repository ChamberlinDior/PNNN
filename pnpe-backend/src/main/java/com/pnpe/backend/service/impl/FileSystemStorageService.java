package com.pnpe.backend.service.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Service
public class FileSystemStorageService {

    @Value("${app.storage.root:uploads/pnpe-documents}")
    private String storageRoot;

    private Path rootPath;

    @PostConstruct
    public void init() throws IOException {
        rootPath = Paths.get(storageRoot).toAbsolutePath().normalize();
        Files.createDirectories(rootPath);
    }

    public String store(String folder, MultipartFile file) {
        try {
            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "document.bin" : file.getOriginalFilename());
            String safeName = LocalDateTime.now().toString().replace(":", "-") + "-" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path targetDir = rootPath.resolve(folder).normalize();
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(safeName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de stocker le document", e);
        }
    }

    public Resource load(String storagePath) {
        return new FileSystemResource(storagePath);
    }

    public String getExtension(String filename) {
        if (filename == null || filename.isBlank() || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public String detectMimeType(String storagePath, String originalFilename, String fallbackMimeType) {
        try {
            Path path = Paths.get(storagePath);
            String detected = Files.probeContentType(path);
            if (detected != null && !detected.isBlank()) {
                return detected;
            }
        } catch (IOException ignored) {
        }

        String extension = getExtension(originalFilename);

        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "bmp" -> "image/bmp";
            case "txt" -> "text/plain";
            case "json" -> "application/json";
            case "xml" -> "application/xml";
            case "html", "htm" -> "text/html";
            case "mp4" -> "video/mp4";
            case "mov" -> "video/quicktime";
            case "webm" -> "video/webm";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "csv" -> "text/csv";
            default -> (fallbackMimeType != null && !fallbackMimeType.isBlank())
                    ? fallbackMimeType
                    : "application/octet-stream";
        };
    }

    public boolean isPreviewable(String mimeType, String filename) {
        String mime = mimeType == null ? "" : mimeType.toLowerCase();
        String extension = getExtension(filename);

        if (mime.startsWith("image/") || mime.startsWith("video/") || mime.startsWith("text/")) {
            return true;
        }

        if ("application/pdf".equals(mime)) {
            return true;
        }

        return switch (extension) {
            case "pdf", "png", "jpg", "jpeg", "gif", "webp", "bmp", "svg", "mp4", "mov", "webm", "txt", "json", "xml", "html", "htm" -> true;
            default -> false;
        };
    }
}