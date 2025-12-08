package org.example.is_lab1.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "import_operations")
public class ImportOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String userId; // UUID пользователя

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatus status;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer fileCount; // количество файлов в операции

    private Integer addedCount; // общее количество добавленных объектов

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    // Связь с файлами (опционально, если нужно хранить метаданные)
    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL)
    private List<ImportFile> files;

    // Конструкторы
    public ImportOperation() {
    }

    // Геттеры
    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public Integer getAddedCount() {
        return addedCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<ImportFile> getFiles() {
        return files;
    }

    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public void setAddedCount(Integer addedCount) {
        this.addedCount = addedCount;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setFiles(List<ImportFile> files) {
        this.files = files;
    }
}

