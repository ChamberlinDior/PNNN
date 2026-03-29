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
}
