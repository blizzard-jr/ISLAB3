package org.example.is_lab1.models.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "import_files")
@Cacheable
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "entity"
)
public class ImportFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "operation_id", nullable = false)
    private ImportOperation operation;

    @Column(nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    private ImportStatus status; // статус обработки этого файла

    private Integer addedCount; // количество объектов из этого файла

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    // Конструкторы
    public ImportFile() {
    }

    // Геттеры
    public Long getId() {
        return id;
    }

    public ImportOperation getOperation() {
        return operation;
    }

    public String getFileName() {
        return fileName;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public Integer getAddedCount() {
        return addedCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setOperation(ImportOperation operation) {
        this.operation = operation;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public void setAddedCount(Integer addedCount) {
        this.addedCount = addedCount;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
