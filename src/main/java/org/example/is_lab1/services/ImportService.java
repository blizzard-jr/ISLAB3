package org.example.is_lab1.services;

import org.example.is_lab1.config.YamlConfig;
import jakarta.persistence.EntityNotFoundException;
import org.example.is_lab1.models.dto.ImportFileDto;
import org.example.is_lab1.models.dto.ImportOperationDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.is_lab1.exceptions.ValidationException;
import org.example.is_lab1.models.dto.BookCreatureDTO;
import org.example.is_lab1.models.dto.ImportRequestDTO;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.ImportFile;
import org.example.is_lab1.models.entity.ImportOperation;
import org.example.is_lab1.models.entity.ImportStatus;
import org.example.is_lab1.repository.ImportFileRepository;
import org.example.is_lab1.repository.ImportOperationRepository;
import org.example.is_lab1.repository.InteractRepository;
import org.example.is_lab1.utils.BookCreatureMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

record FileContent(String fileName, byte[] content) {}

@Service
@Slf4j
public class ImportService {
    private final InteractRepository creatureRepository;
    private final ImportOperationRepository importOperationRepository;
    private final ImportFileRepository importFileRepository;
    private final BookCreatureMapper mapper;
    private final ConflictLogger conflictLogger;
    private final WebSocketNotificationService notificationService;
    private final MinioStorageService minioStorageService;
    

    @Lazy
    @Autowired
    private ImportService self;
    @Autowired
    private ImportStatusService importStatusService;

    public ImportService(InteractRepository creatureRepository,
                         ImportOperationRepository importOperationRepository,
                         ImportFileRepository importFileRepository,
                         BookCreatureMapper mapper,
                         ConflictLogger conflictLogger,
                         WebSocketNotificationService notificationService, MinioStorageService minioStorageService) {
        this.creatureRepository = creatureRepository;
        this.importOperationRepository = importOperationRepository;
        this.importFileRepository = importFileRepository;
        this.mapper = mapper;
        this.conflictLogger = conflictLogger;
        this.notificationService = notificationService;
        this.minioStorageService = minioStorageService;
    }

    public Long importFromYaml(List<MultipartFile> files, String userId) {
        ImportOperation operation = new ImportOperation();
        operation.setUserId(userId);
        operation.setStatus(ImportStatus.PENDING);
        operation.setStartTime(LocalDateTime.now());
        operation.setFileCount(files.size());
        operation.setAddedCount(0);
        final ImportOperation savedOperation = importOperationRepository.save(operation);

        List<ImportFile> importFiles = files.stream()
                .map(fc -> {
                    ImportFile importFile = new ImportFile();
                    importFile.setOperation(savedOperation);
                    importFile.setFileName(fc.getOriginalFilename());
                    log.warn(fc.getName());
                    importFile.setStatus(ImportStatus.PENDING);
                    return importFile;
                })
                .collect(Collectors.toList());
        importFiles = importFileRepository.saveAll(importFiles);
        savedOperation.setFiles(importFiles);
        try {
            self.processImportAsync(savedOperation.getId(), files);
        }catch(Exception e){
            operation.setStatus(ImportStatus.FAILED);
            operation.setErrorMessage("Что-то пошло не так. Произошёл откат транзакции");
            operation.setFiles(null);
            operation.setEndTime(LocalDateTime.now());
            importOperationRepository.save(operation);
        }
        return savedOperation.getId();
    }


//    @Async
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public void processImportAsync(Long operationId, List<MultipartFile> files) {
        ImportOperation operation = importOperationRepository.findById(operationId)
                .orElseThrow(() -> new EntityNotFoundException("Operation not found"));

        String key = null;
        ImportFile importFile = null;
        try {
            conflictLogger.clearLog();
            operation.setStatus(ImportStatus.IN_PROGRESS);
            importOperationRepository.save(operation);

            int totalAdded = 0;
            boolean allSuccess = true;
            StringBuilder errors = new StringBuilder();

            for (MultipartFile fc : files) {
                importFile = operation.getFiles().stream()
                        .filter(f -> f.getFileName().equals(fc.getOriginalFilename()))
                        .findFirst()
                        .orElseThrow();
                String importId = UUID.randomUUID().toString();
                key = minioStorageService.upload(fc, importId);
                importFile.setMinioKey(key);
                importFileRepository.save(importFile);
                FileImportResult result = processSingleFile(new FileContent(fc.getOriginalFilename(), fc.getBytes()), operation, key);
                totalAdded += result.addedCount();

            }

            operation.setStatus(ImportStatus.SUCCESS);
            operation.setAddedCount(totalAdded);
            operation.setEndTime(LocalDateTime.now());

            if (!allSuccess) {
                operation.setErrorMessage(truncateErrorMessage(errors.toString()));
            }

            importOperationRepository.save(operation);

        } catch (Exception e) {
            minioStorageService.delete(key, importFile);
            log.error("Error processing import operation {}", operationId, e);
            throw new RuntimeException(e);
        }
    }
    

    private String truncateErrorMessage(String message) {
        if (message == null) return null;
        return message.length() > 2000 ? message.substring(0, 2000) + "..." : message;
    }

    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    protected FileImportResult processSingleFile(FileContent fileContent, ImportOperation operation, String key) {
//        for(ImportFile el : operation.getFiles()){
//            log.warn(el.getFileName() + "filename from operation");
//            log.warn(fileContent.fileName() + "filename from filecontent");
//        }
        ImportFile importFile = operation.getFiles().stream()
                .filter(f -> f.getFileName().equals(fileContent.fileName()))
                .findFirst()
                .orElseThrow();

        try {
            importFile.setStatus(ImportStatus.IN_PROGRESS);
            importFile.setMinioKey(key);
            importFileRepository.save(importFile);

            ImportRequestDTO request = YamlConfig.getYamlMapper().readValue(
                    fileContent.content(),
                    ImportRequestDTO.class
            );

            int addedCount = 0;

            for (BookCreatureDTO dto : request.creatures()) {
                log.warn("Добавляем сущность: " + dto.name());
                validateCreature(dto);
                Optional<BookCreature> existing = creatureRepository.findByName(dto.name());
                log.warn("Проверяем сущность: " + (existing.isPresent() ? "Найдена с именем: " + existing.get().getName() : "Сущность не найдена с таким именем"));
                if (existing.isPresent()) {
                    BookCreature resolved = resolveConflict(
                            existing.get(),
                            dto,
                            operation.getId()
                    );
                    creatureRepository.save(resolved);
                } else {
                    BookCreature creature = mapper.toEntity(dto);
                    creatureRepository.save(creature);
                    addedCount++;
                    notificationService.notifyCreated(creature.getId(), mapper.toDto(creature));
                }


            }

            importFile.setStatus(ImportStatus.SUCCESS);
            importFile.setAddedCount(addedCount);
            importFileRepository.save(importFile);

            return new FileImportResult(ImportStatus.SUCCESS, addedCount, null);

        } catch (Exception e) {
            log.error("Error processing file {}", fileContent.fileName(), e);
            importStatusService.markFailed(importFile, "Проблема с файлом " + importFile.getFileName() + ", проверьте ограничения уникальности и формат файла");
//            importFile.setStatus(ImportStatus.FAILED);
//            importFile.setErrorMessage(truncateErrorMessage(e.getMessage()));
//            importFileRepository.save(importFile);
            throw new RuntimeException(e);
        }
    }

    private BookCreature resolveConflict(BookCreature existing,
                                         BookCreatureDTO incoming,
                                         Long operationId) {
        // Сравниваем по creationDate - более ранняя запись приоритетнее
        Date existingDate = existing.getCreationDate();
        Date incomingDate = new Date(); // Используем текущую дату для входящей

        String resolution;
        BookCreature result;

        if (incomingDate.before(existingDate)) {
            // Входящая запись раньше - обновляем существующую
            mapper.updateEntityFromDto(incoming, existing);
            result = existing;
            resolution = "Updated existing with earlier record";
        } else {
            // Существующая раньше - оставляем как есть
            result = existing;
            resolution = "Kept existing record (earlier)";
        }

        // Логируем конфликт
        conflictLogger.logConflict(
                operationId,
                existing.getName(),
                incoming.name(),
                resolution
        );

        return result;
    }

    private void validateCreature(BookCreatureDTO dto) {
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new ValidationException("Name cannot be null or empty");
        }
        if (dto.age() <= 0) {
            throw new ValidationException("Age must be greater than 0");
        }
        if (dto.defenseLevel() == null || dto.defenseLevel() <= 0) {
            throw new ValidationException("Defense level must be greater than 0");
        }
        if (dto.creatureType() == null || dto.creatureType().trim().isEmpty()) {
            throw new ValidationException("Creature type cannot be null or empty");
        }
        if (dto.attackLevel() != null && dto.attackLevel() <= 0) {
            throw new ValidationException("Attack level must be greater than 0 if provided");
        }
    }


    public ImportOperationDto getOperation(Long id) {
        ImportOperation operation = importOperationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Operation not found with id " + id));
        return mapToDto(operation);
    }


    public Page<ImportOperationDto> getHistory(String userId, Pageable pageable) {
        Page<ImportOperation> operations;
        if (userId == null) {
            // Админ видит все операции
            operations = importOperationRepository.findAll(pageable);
        } else {
            // Пользователь видит только свои операции
            operations = importOperationRepository.findByUserId(userId, pageable);
        }
        return operations.map(this::mapToDto);
    }


    private ImportOperationDto mapToDto(ImportOperation operation) {
        List<ImportFileDto> fileDtos = null;
        if (operation.getFiles() != null) {
            fileDtos = operation.getFiles().stream()
                    .map(f -> new ImportFileDto(
                            f.getId(),
                            f.getFileName(),
                            f.getStatus(),
                            f.getAddedCount(),
                            f.getErrorMessage()
                    ))
                    .collect(Collectors.toList());
        }

        return new ImportOperationDto(
                operation.getId(),
                operation.getUserId(),
                operation.getStatus(),
                operation.getStartTime(),
                operation.getEndTime(),
                operation.getFileCount(),
                operation.getAddedCount(),
                operation.getErrorMessage(),
                fileDtos
        );
    }
}


record FileImportResult(ImportStatus status, int addedCount, String errorMessage) {}
