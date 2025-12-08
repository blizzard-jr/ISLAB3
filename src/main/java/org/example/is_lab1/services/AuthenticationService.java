package org.example.is_lab1.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.is_lab1.models.dto.AuthenticationResponseDto;
import org.example.is_lab1.models.dto.RegistrationRequestDto;
import org.example.is_lab1.models.entity.Role;
import org.example.is_lab1.models.entity.Token;
import org.example.is_lab1.models.entity.User;
import org.example.is_lab1.repository.TokenRepository;
import org.example.is_lab1.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public AuthenticationService(UserRepository userRepository,
                                 JWTService jwtService,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Регистрация нового пользователя.
     * @throws IllegalArgumentException если пользователь уже существует
     */
    public AuthenticationResponseDto register(RegistrationRequestDto request) {
        // Проверяем, что пользователь не существует
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        // Если роль не указана, устанавливаем USER по умолчанию
        user.setRole(request.role() != null ? request.role() : Role.USER);

        userRepository.save(user);

        // Генерируем токены для нового пользователя
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(accessToken, refreshToken, user);

        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    private void revokeAllToken(User user) {
        List<Token> validTokens = tokenRepository.findAllAccessTokenByUser(user.getId());

        if (!validTokens.isEmpty()) {
            validTokens.forEach(t -> t.setLoggedOut(true));
        }

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = new Token();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepository.save(token);
    }

    /**
     * Аутентификация существующего пользователя.
     * 
     * Защита от race condition при конкурентных логинах:
     * 1. @Transactional - гарантирует атомарность операций
     * 2. findByUsernameWithLock - пессимистическая блокировка FOR UPDATE блокирует пользователя
     *    до завершения транзакции, предотвращая одновременные логины одного пользователя
     * 3. Порядок операций: сначала отзываем старые токены, потом сохраняем новый
     * 
     * @throws org.springframework.security.core.AuthenticationException если учетные данные неверны
     */
    @Transactional
    public AuthenticationResponseDto authenticate(RegistrationRequestDto request) {
        // Аутентификация через Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // Получаем пользователя с пессимистической блокировкой
        // Это гарантирует, что только один поток может работать с этим пользователем одновременно
        User user = userRepository.findByUsernameWithLock(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Генерируем новые токены
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Сначала отзываем все старые токены пользователя
        revokeAllToken(user);
        
        // Затем сохраняем новый токен
        // Порядок важен: сначала отзыв, потом сохранение нового
        saveUserToken(accessToken, refreshToken, user);

        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if (jwtService.isValidRefresh(token, user)) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllToken(user);
            saveUserToken(accessToken, refreshToken, user);

            return new ResponseEntity<>(new AuthenticationResponseDto(accessToken, refreshToken), HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
