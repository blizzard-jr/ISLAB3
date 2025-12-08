package org.example.is_lab1.repository;

import org.example.is_lab1.models.entity.Token;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
            SELECT t FROM Token t inner join User u
            on t.user.id = u.id
            where t.user.id = :userId and t.loggedOut = false
            """)
    List<Token> findAllAccessTokenByUser(String userId);

    /**
     * Поиск активного токена по accessToken.
     * Возвращает только токены, которые не помечены как loggedOut.
     * Pageable с размером 1 гарантирует возврат максимум одного результата.
     * ORDER BY t.id DESC берёт самый новый токен при дубликатах.
     */
    @Query("SELECT t FROM Token t WHERE t.accessToken = :accessToken AND t.loggedOut = false ORDER BY t.id DESC")
    List<Token> findByAccessTokenAndLoggedOutFalse(String accessToken, Pageable pageable);
    
    /**
     * Поиск первого активного токена по accessToken.
     * Использует findByAccessTokenAndLoggedOutFalse с Pageable для гарантии одного результата.
     */
    default Optional<Token> findFirstByAccessTokenAndLoggedOutFalse(String accessToken) {
        List<Token> tokens = findByAccessTokenAndLoggedOutFalse(accessToken, Pageable.ofSize(1));
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    /**
     * Поиск активного токена по refreshToken.
     * Возвращает только токены, которые не помечены как loggedOut.
     * Pageable с размером 1 гарантирует возврат максимум одного результата.
     * ORDER BY t.id DESC берёт самый новый токен при дубликатах.
     */
    @Query("SELECT t FROM Token t WHERE t.refreshToken = :refreshToken AND t.loggedOut = false ORDER BY t.id DESC")
    List<Token> findByRefreshTokenAndLoggedOutFalse(String refreshToken, Pageable pageable);
    
    /**
     * Поиск первого активного токена по refreshToken.
     * Использует findByRefreshTokenAndLoggedOutFalse с Pageable для гарантии одного результата.
     */
    default Optional<Token> findFirstByRefreshTokenAndLoggedOutFalse(String refreshToken) {
        List<Token> tokens = findByRefreshTokenAndLoggedOutFalse(refreshToken, Pageable.ofSize(1));
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }
}
