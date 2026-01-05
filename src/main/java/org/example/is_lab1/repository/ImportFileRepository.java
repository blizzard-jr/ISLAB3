package org.example.is_lab1.repository;

import org.example.is_lab1.models.entity.ImportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportFileRepository extends JpaRepository<ImportFile, Long> {
    // Базовые методы CRUD предоставляются JpaRepository
    // При необходимости можно добавить кастомные методы
    ImportFile findByMinioKey(String key);
}












