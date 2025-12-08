package org.example.is_lab1.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.is_lab1.models.entity.User;
import org.example.is_lab1.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${security.jwt.secret_key}")
    private String secretKey;

    @Value("${security.jwt.access_token_expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh_token_expiration}")
    private long refreshTokenExpiration;

    private final TokenRepository tokenRepository;

    public JWTService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    private SecretKey getSgningKey() {

        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Генерация JWT токена с уникальным идентификатором.
     * UUID добавляется в claims для гарантии уникальности каждого токена,
     * даже если два пользователя логинятся одновременно.
     */
    private String generateToken(User user, long expiryTime) {
        long now = System.currentTimeMillis();
        String uniqueId = UUID.randomUUID().toString();
        
        JwtBuilder builder = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryTime))
                .claim("jti", uniqueId)  // JWT ID - уникальный идентификатор токена
                .signWith(getSgningKey());

        return builder.compact();
    }
    public String generateAccessToken(User user) {

        return generateToken(user, accessTokenExpiration);
    }
    public String generateRefreshToken(User user) {

        return generateToken(user, refreshTokenExpiration);
    }
    private Claims extractAllClaims(String token) {

        JwtParserBuilder parser = Jwts.parser();
        parser.verifyWith(getSgningKey());

        return parser.build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private boolean isAccessTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }
    /**
     * Валидация access token.
     * 
     * Логика:
     * 1. Проверяем, что токен существует в БД И активен (loggedOut = false)
     *    - Если токен не найден → он невалидный (возвращаем false)
     * 2. Извлекаем username из токена (только если токен найден в БД)
     * 3. Проверяем совпадение username
     * 4. Проверяем, что токен не истёк
     * 
     * Порядок проверок важен:
     * - Сначала проверяем наличие токена в БД (быстрая проверка)
     * - Потом извлекаем username (может выбросить исключение, если токен невалидный)
     * - В конце проверяем срок действия
     * 
     * Почему фильтруем только активные токены:
     * - Если токен помечен как loggedOut = true, он уже недействителен
     * - Метод findFirstByAccessTokenAndLoggedOutFalse уже фильтрует только активные
     * - Поэтому если токен найден → он валидный, если нет → невалидный
     */
    public boolean isValid(String token, UserDetails user) {
        try {
            // Сначала проверяем наличие активного токена в БД
            // Это быстрая проверка, которая не требует парсинга токена
            boolean isValidToken = tokenRepository.findFirstByAccessTokenAndLoggedOutFalse(token)
                    .isPresent();

            if (!isValidToken) {
                return false;  // Токен не найден в БД или отозван
            }

            // Теперь извлекаем username из токена
            // Это может выбросить исключение, если токен невалидный или истёк
            String username = extractUsername(token);

            // Проверяем совпадение username
            if (!username.equals(user.getUsername())) {
                return false;
            }

            // Проверяем, что токен не истёк
            // isAccessTokenExpired возвращает true, если токен НЕ истёк
            if (!isAccessTokenExpired(token)) {
                return false;  // Токен истёк
            }

            return true;
        } catch (Exception e) {
            // Если произошла ошибка при парсинге токена или валидации,
            // считаем токен невалидным
            return false;
        }
    }

    /**
     * Валидация refresh token.
     * Аналогично isValid(), но для refresh token.
     */
    public boolean isValidRefresh(String token, User user) {

        String username = extractUsername(token);

        // Поиск активного refresh token в БД (loggedOut = false)
        boolean isValidRefreshToken = tokenRepository.findFirstByRefreshTokenAndLoggedOutFalse(token)
                .isPresent();

        return username.equals(user.getUsername())
                && isAccessTokenExpired(token)
                && isValidRefreshToken;
    }
}
