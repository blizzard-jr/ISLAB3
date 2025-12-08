package org.example.is_lab1.controllers;

import org.example.is_lab1.models.dto.ImportOperationDto;
import org.example.is_lab1.models.entity.Role;
import org.example.is_lab1.models.entity.User;
import org.example.is_lab1.services.ImportService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/import")
public class ImportController {
    private final ImportService importService;

    public ImportController(ImportService service) {
        this.importService = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<ImportOperationDto> uploadFiles(
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal User user) {

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one file is required");
        }
        if (files.size() > 10) {
            throw new IllegalArgumentException("Allowed 3 to 10 files");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".yaml") && !filename.endsWith(".yml"))) {
                throw new IllegalArgumentException("Only YAML files are allowed");
            }
        }

        String userId = user.getId();
        Long operationId = importService.importFromYaml(files, userId);
        return ResponseEntity.ok(importService.getOperation(operationId));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<ImportOperationDto>> getHistory(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        boolean isAdmin = user.getRole() == Role.ADMIN;
        return ResponseEntity.ok(importService.getHistory(
                isAdmin ? null : user.getId(),
                pageable
        ));
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> getConflictFile() {
        Path filePath = Paths.get(System.getProperty("user.dir"), "conflicts.txt");

        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(filePath.getParent() != null ? filePath.getParent() : Paths.get("."));
                Files.createFile(filePath);
                Files.write(filePath, "No conflicts found.\n".getBytes());
            } catch (IOException e) {
                return ResponseEntity.notFound().build();
            }
        }

        Resource resource = new FileSystemResource(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"conflicts.txt\"")
                .body(resource);
    }
}
