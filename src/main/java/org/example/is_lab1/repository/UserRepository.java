package org.example.is_lab1.repository;

import jakarta.persistence.LockModeType;
import org.example.is_lab1.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    /**
     * Получить пользователя с пессимистической блокировкой FOR UPDATE.
     * Используется для предотвращения race condition при конкурентных логинах:
     * - Блокирует пользователя до завершения транзакции
     * - Гарантирует атомарность операций revokeAllToken и saveUserToken
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameWithLock(@Param("username") String username);
}
