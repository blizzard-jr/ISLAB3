package org.example.is_lab1.repository;

import org.example.is_lab1.models.entity.ImportOperation;
import org.example.is_lab1.models.entity.ImportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportOperationRepository extends JpaRepository<ImportOperation, Long> {
    /**
     * Найти все операции импорта по UUID пользователя
     */
    Page<ImportOperation> findByUserId(String userId, Pageable pageable);
    
    /**
     * Найти все операции импорта по статусу
     */
    List<ImportOperation> findByStatus(ImportStatus status);
    
    /**
     * Подсчитать количество операций по статусу
     */
    long countByStatus(ImportStatus status);
}










