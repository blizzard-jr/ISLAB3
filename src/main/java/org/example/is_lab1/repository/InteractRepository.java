package org.example.is_lab1.repository;

import jakarta.persistence.LockModeType;
import org.example.is_lab1.models.entity.BookCreature;
import org.example.is_lab1.models.entity.MagicCity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InteractRepository extends JpaRepository<BookCreature, Integer> {
    boolean existsByCreatureLocation(MagicCity city);

    Page<BookCreature> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Найти существо по точному имени (для проверки дубликатов при импорте)
     */
    Optional<BookCreature> findByName(String name);
    
    /**
     * Получить существо с пессимистической блокировкой FOR UPDATE.
     * Используется для предотвращения lost update при конкурентном редактировании.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM BookCreature c WHERE c.id = :id")
    Optional<BookCreature> findByIdWithLock(@Param("id") int id);
}
