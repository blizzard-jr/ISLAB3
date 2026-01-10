# Spring Security: Глубокий разбор теории и практики

## Содержание
1. [Введение в Spring Security](#введение)
2. [Архитектура Spring Security](#архитектура)
3. [Цикл обработки запроса](#цикл-обработки-запроса)
4. [Разбор реализации в нашем приложении](#разбор-реализации)
5. [Детальный анализ компонентов](#детальный-анализ)
6. [JWT токены и их валидация](#jwt-токены)
7. [Безопасность и защита от атак](#безопасность)

---

## Введение в Spring Security

### Что такое Spring Security?

Spring Security — это мощный фреймворк для обеспечения безопасности в Spring-приложениях. Он предоставляет комплексное решение для:
- **Аутентификации (Authentication)** — процесс проверки личности пользователя
- **Авторизации (Authorization)** — процесс проверки прав доступа к ресурсам
- **Защиты от атак** — CSRF, XSS, SQL Injection и др.

### Основные концепции

#### 1. Authentication (Аутентификация)
**Вопрос:** "Кто вы?"
**Ответ:** Проверка учетных данных (username/password, токен, сертификат)

#### 2. Authorization (Авторизация)
**Вопрос:** "Что вам разрешено делать?"
**Ответ:** Проверка прав доступа на основе ролей/прав пользователя

#### 3. Principal (Принципал)
Объект, представляющий аутентифицированного пользователя

#### 4. GrantedAuthority (Права доступа)
Права или роли, которыми обладает пользователь (например, "ADMIN", "USER")

---

## Архитектура Spring Security

### Фильтровая цепочка (Filter Chain)

Spring Security работает на основе **Servlet Filter Chain** — цепочки фильтров, которые обрабатывают каждый HTTP-запрос до того, как он достигнет контроллера.

```
HTTP Request
    ↓
[Filter 1] → [Filter 2] → [Filter 3] → ... → [Filter N]
    ↓
DispatcherServlet
    ↓
Controller
```

### Ключевые компоненты

#### 1. SecurityFilterChain
Основная цепочка фильтров безопасности. Определяет:
- Какие URL требуют аутентификации
- Какие роли имеют доступ к ресурсам
- Какие фильтры применять

#### 2. AuthenticationManager
Центральный компонент для аутентификации. Делегирует работу провайдерам:
```java
AuthenticationManager
    ↓
ProviderManager
    ↓
AuthenticationProvider (например, DaoAuthenticationProvider)
```

#### 3. UserDetailsService
Интерфейс для загрузки данных пользователя из источника (БД, LDAP и т.д.)

#### 4. PasswordEncoder
Компонент для хеширования и проверки паролей

#### 5. SecurityContext
Хранит информацию об аутентифицированном пользователе в текущем потоке выполнения

---

## Цикл обработки запроса

### Полный жизненный цикл запроса в Spring Security

```
1. HTTP Request поступает в приложение
   ↓
2. SecurityFilterChain начинает обработку
   ↓
3. CORS фильтр проверяет cross-origin запросы
   ↓
4. CSRF фильтр проверяет токен (если включен)
   ↓
5. JwtFilter (наш кастомный фильтр):
   - Извлекает токен из заголовка Authorization
   - Валидирует токен
   - Загружает UserDetails
   - Создает Authentication объект
   - Устанавливает в SecurityContext
   ↓
6. Authorization фильтр:
   - Проверяет правила доступа (authorizeHttpRequests)
   - Сравнивает роли пользователя с требуемыми
   ↓
7. Если доступ разрешен → запрос идет в Controller
   Если доступ запрещен → AccessDeniedException или AuthenticationException
   ↓
8. Response возвращается клиенту
```

### Детальный разбор каждого этапа

#### Этап 1: Входящий HTTP-запрос
```http
GET /api/users HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Этап 2: SecurityFilterChain активируется
Spring Security анализирует конфигурацию и определяет, какие фильтры применить.

#### Этап 3: CORS фильтр
Проверяет, разрешен ли запрос с данного origin:
```java
// Из SecurityConfig.java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

#### Этап 4: JWT фильтр (наш кастомный)
```java
// JwtFilter.doFilterInternal()
1. Извлекает токен: "Bearer <token>"
2. Парсит токен и извлекает username
3. Загружает UserDetails через UserService
4. Валидирует токен через JWTService
5. Создает UsernamePasswordAuthenticationToken
6. Устанавливает в SecurityContext
```

#### Этап 5: Authorization проверка
```java
// SecurityConfig.java, строка 75-86
http.authorizeHttpRequests(auth -> {
    auth.requestMatchers("/admin/**").hasAuthority("ADMIN");
    auth.anyRequest().authenticated();
})
```

Spring Security проверяет:
- Есть ли Authentication в SecurityContext?
- Есть ли у пользователя требуемая роль/право?

#### Этап 6: Обработка исключений
Если аутентификация не прошла:
```java
.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
// Возвращает 401 Unauthorized
```

Если авторизация не прошла:
```java
.accessDeniedHandler(accessDeniedHandler())
// Возвращает 403 Forbidden
```

---

## Разбор реализации в нашем приложении

### 1. SecurityConfig.java — Конфигурация безопасности

#### Класс и аннотации
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
```

**@Configuration** — указывает Spring, что это класс конфигурации
**@EnableWebSecurity** — включает Spring Security и отключает дефолтную конфигурацию

#### Bean: PasswordEncoder
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Что делает:**
- Хеширует пароли с использованием алгоритма BCrypt
- BCrypt — односторонняя хеш-функция с солью (salt)
- Каждый раз генерирует разный хеш для одного пароля (благодаря соли)

**Как работает BCrypt:**
```
Пароль: "password123"
Соль: автоматически генерируется
Хеш: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
```

**Почему BCrypt:**
- Медленный алгоритм (защита от brute-force)
- Встроенная соль (защита от rainbow tables)
- Адаптивный (можно увеличить сложность)

#### Bean: AuthenticationManager
```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

**Что делает:**
- Управляет процессом аутентификации
- Используется в AuthenticationService для проверки username/password

**Как работает:**
```java
// В AuthenticationService.authenticate()
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(username, password)
);
```

**Внутренний процесс:**
1. AuthenticationManager получает UsernamePasswordAuthenticationToken
2. Находит подходящий AuthenticationProvider (DaoAuthenticationProvider)
3. DaoAuthenticationProvider использует UserDetailsService для загрузки пользователя
4. Сравнивает пароль через PasswordEncoder
5. Если совпадает → возвращает аутентифицированный Authentication
6. Если нет → выбрасывает AuthenticationException

#### Bean: SecurityFilterChain
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, JWTService jwtService, UserService userService) {
    // Конфигурация...
}
```

**HttpSecurity** — билдер для настройки безопасности

##### Отключение CSRF
```java
http.csrf(AbstractHttpConfigurer::disable)
```

**CSRF (Cross-Site Request Forgery)** — атака, когда злоумышленник заставляет пользователя выполнить действие на сайте, где он аутентифицирован.

**Почему отключен:**
- Мы используем JWT токены (stateless)
- CSRF защита нужна для stateful приложений (с сессиями)
- JWT токены не уязвимы к CSRF (токен в заголовке, не в cookie)

##### CORS конфигурация
```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

**CORS (Cross-Origin Resource Sharing)** — механизм для разрешения запросов с других доменов.

**Наша конфигурация:**
```java
configuration.setAllowedOriginPatterns(List.of("*"));  // Разрешаем все origin
configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
configuration.setAllowedHeaders(List.of("*"));
configuration.setAllowCredentials(true);  // Разрешаем cookies/credentials
```

**⚠️ Внимание:** `allowedOriginPatterns("*")` с `allowCredentials(true)` может быть небезопасно в production!

##### Правила авторизации
```java
http.authorizeHttpRequests(auth -> {
    auth.requestMatchers(
        "/login/**",
        "/registration/**",
        "/refresh_token/**",
        "/ws/**",
        "/css/**",
        "/"
    ).permitAll();  // Эти URL доступны без аутентификации
    
    auth.requestMatchers("/admin/**").hasAuthority("ADMIN");  // Требует роль ADMIN
    
    auth.anyRequest().authenticated();  // Все остальные требуют аутентификации
})
```

**Порядок важен!** Spring Security проверяет правила сверху вниз и останавливается на первом совпадении.

##### UserDetailsService
```java
.userDetailsService(userService)
```

Указывает Spring Security, какой сервис использовать для загрузки пользователей.

##### Обработка исключений
```java
.exceptionHandling(e -> {
    e.accessDeniedHandler(accessDeniedHandler());  // 403 Forbidden
    e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));  // 401 Unauthorized
})
```

**AuthenticationEntryPoint** — вызывается, когда пользователь не аутентифицирован
**AccessDeniedHandler** — вызывается, когда пользователь аутентифицирован, но не имеет прав

##### Stateless сессии
```java
.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
```

**STATELESS** означает:
- Spring Security не создает HTTP сессии
- Каждый запрос обрабатывается независимо
- Аутентификация происходит через JWT токен в каждом запросе

**Почему STATELESS:**
- Масштабируемость (не нужны sticky sessions)
- RESTful архитектура
- Микросервисы

##### JWT фильтр
```java
.addFilterBefore(new JwtFilter(jwtService, userService), UsernamePasswordAuthenticationFilter.class)
```

**addFilterBefore** — добавляет наш фильтр ПЕРЕД стандартным UsernamePasswordAuthenticationFilter.

**Почему перед:**
- Наш JwtFilter должен обработать JWT токен и установить Authentication
- UsernamePasswordAuthenticationFilter используется для form-based login (нам не нужен)

##### Logout обработка
```java
.logout(log -> {
    log.logoutUrl("/logout");
    log.addLogoutHandler(logoutHandler());
    log.logoutSuccessHandler((request, response, authentication) ->
        SecurityContextHolder.clearContext());
})
```

**LogoutHandler** — выполняет действия при выходе (в нашем случае помечает токен как недействительный)
**LogoutSuccessHandler** — вызывается после успешного выхода

---

### 2. JwtFilter.java — Кастомный фильтр для JWT

#### Наследование от OncePerRequestFilter
```java
public class JwtFilter extends OncePerRequestFilter {
```

**OncePerRequestFilter** — гарантирует, что фильтр выполнится только один раз за запрос (даже если есть forward/include).

#### Метод doFilterInternal
```java
@Override
protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain filterChain) {
    // Логика фильтра
}
```

**FilterChain** — цепочка фильтров. Вызов `filterChain.doFilter()` передает запрос дальше.

#### Извлечение токена
```java
String authHeader = request.getHeader("Authorization");

if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    filterChain.doFilter(request, response);
    return;  // Если токена нет, просто пропускаем запрос
}

String token = authHeader.substring(7);  // Убираем "Bearer "
```

**Формат:** `Authorization: Bearer <token>`

#### Валидация и установка Authentication
```java
String username = jwtService.extractUsername(token);

if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    UserDetails userDetails = userService.loadUserByUsername(username);
    
    if (jwtService.isValid(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(
                userDetails,      // Principal
                null,             // Credentials (не нужны, т.к. уже аутентифицированы)
                userDetails.getAuthorities()  // Роли/права
            );
        
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
```

**UsernamePasswordAuthenticationToken** — реализация Authentication для username/password.

**Параметры:**
1. **Principal** — UserDetails (пользователь)
2. **Credentials** — null (пароль не нужен, т.к. токен уже валидирован)
3. **Authorities** — роли пользователя

**SecurityContextHolder** — хранит Authentication в ThreadLocal (потокобезопасно).

**WebAuthenticationDetails** — дополнительная информация о запросе (IP, session ID и т.д.).

#### Обработка ошибок
```java
catch (Exception e) {
    log.warn("JwtFilter: Exception during token validation: {}", e.getMessage());
}
// filterChain.doFilter() вызывается в любом случае
```

**Почему не выбрасываем исключение:**
- Если токен невалидный, запрос просто пройдет без Authentication
- SecurityFilterChain проверит правила и вернет 401, если требуется аутентификация

---

### 3. UserService.java — Загрузка пользователей

#### Реализация UserDetailsService
```java
@Service
public class UserService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
```

**UserDetailsService** — интерфейс Spring Security для загрузки пользователей.

**Когда вызывается:**
- В JwtFilter для загрузки UserDetails по username из токена
- В AuthenticationManager при аутентификации через username/password

#### User.java — Реализация UserDetails
```java
@Entity
public class User implements UserDetails {
    private String username;
    private String password;
    private Role role;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return true; }
}
```

**UserDetails** — интерфейс, представляющий пользователя в Spring Security.

**Методы:**
- `getAuthorities()` — роли/права пользователя
- `isAccountNonExpired()` — не истекла ли учетная запись
- `isAccountNonLocked()` — не заблокирована ли учетная запись
- `isCredentialsNonExpired()` — не истекли ли учетные данные
- `isEnabled()` — активна ли учетная запись

**В нашем случае:** все методы возвращают `true` (упрощенная реализация).

---

### 4. JWTService.java — Работа с JWT токенами

#### Что такое JWT?

**JWT (JSON Web Token)** — компактный способ безопасной передачи информации между сторонами.

**Структура JWT:**
```
header.payload.signature
```

**Пример:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTYxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**Расшифровка:**
1. **Header** (base64): `{"alg":"HS256","typ":"JWT"}`
2. **Payload** (base64): `{"sub":"user1","exp":1616239022}`
3. **Signature**: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)

#### Генерация секретного ключа
```java
private SecretKey getSgningKey() {
    byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
}
```

**HMAC-SHA256** — алгоритм для подписи токена.

**Секретный ключ** хранится в `application.properties`:
```properties
security.jwt.secret_key=4eeab38d706831be4b36612ead768ef8182d1dd6f0e14e5dc934652e297fb16a
```

**⚠️ Важно:** В production используйте переменные окружения или секретный менеджер!

#### Генерация токена
```java
private String generateToken(User user, long expiryTime) {
    long now = System.currentTimeMillis();
    String uniqueId = UUID.randomUUID().toString();
    
    JwtBuilder builder = Jwts.builder()
        .subject(user.getUsername())           // sub claim
        .issuedAt(new Date(now))              // iat claim
        .expiration(new Date(now + expiryTime)) // exp claim
        .claim("jti", uniqueId)               // JWT ID (уникальный идентификатор)
        .signWith(getSgningKey());            // Подпись
        
    return builder.compact();
}
```

**Claims (утверждения) в токене:**
- `sub` (subject) — username пользователя
- `iat` (issued at) — время создания
- `exp` (expiration) — время истечения
- `jti` (JWT ID) — уникальный идентификатор токена

**Access Token:** 36000000 мс = 10 часов
**Refresh Token:** 252000000 мс = 7 дней

#### Извлечение данных из токена
```java
private Claims extractAllClaims(String token) {
    JwtParserBuilder parser = Jwts.parser();
    parser.verifyWith(getSgningKey());  // Проверяет подпись
    
    return parser.build()
        .parseSignedClaims(token)
        .getPayload();
}

public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}
```

**Парсинг токена:**
1. Декодирует base64 части
2. Проверяет подпись (если неверная → исключение)
3. Проверяет срок действия (если истек → исключение)
4. Возвращает Claims

#### Валидация токена
```java
public boolean isValid(String token, UserDetails user) {
    try {
        // 1. Проверяем наличие активного токена в БД
        boolean isValidToken = tokenRepository
            .findFirstByAccessTokenAndLoggedOutFalse(token)
            .isPresent();
        
        if (!isValidToken) {
            return false;  // Токен отозван или не существует
        }
        
        // 2. Извлекаем username из токена
        String username = extractUsername(token);
        
        // 3. Проверяем совпадение username
        if (!username.equals(user.getUsername())) {
            return false;
        }
        
        // 4. Проверяем срок действия
        if (!isAccessTokenExpired(token)) {
            return false;  // Токен истек
        }
        
        return true;
    } catch (Exception e) {
        return false;  // Ошибка парсинга
    }
}
```

**Почему проверяем в БД:**
- Даже если токен валидный по подписи и сроку, он может быть отозван (logout)
- Поле `loggedOut` в таблице `token_table` указывает на статус токена

**Порядок проверок важен:**
1. Сначала БД (быстро, не требует парсинга)
2. Потом парсинг (может выбросить исключение)
3. В конце проверка срока действия

---

### 5. AuthenticationService.java — Бизнес-логика аутентификации

#### Регистрация пользователя
```java
public AuthenticationResponseDto register(RegistrationRequestDto request) {
    // Проверка существования пользователя
    if (userRepository.findByUsername(request.username()).isPresent()) {
        throw new IllegalArgumentException("Пользователь уже существует");
    }
    
    // Создание пользователя
    User user = new User();
    user.setUsername(request.username());
    user.setPassword(passwordEncoder.encode(request.password()));  // Хеширование пароля
    user.setRole(request.role() != null ? request.role() : Role.USER);
    
    userRepository.save(user);
    
    // Генерация токенов
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    
    saveUserToken(accessToken, refreshToken, user);
    
    return new AuthenticationResponseDto(accessToken, refreshToken);
}
```

**Процесс:**
1. Проверка уникальности username
2. Хеширование пароля через BCrypt
3. Сохранение пользователя
4. Генерация токенов
5. Сохранение токенов в БД

#### Аутентификация пользователя
```java
@Transactional
public AuthenticationResponseDto authenticate(RegistrationRequestDto request) {
    // 1. Аутентификация через Spring Security
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.username(),
            request.password()
        )
    );
    
    // 2. Получение пользователя с блокировкой
    User user = userRepository.findByUsernameWithLock(request.username())
        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    
    // 3. Генерация новых токенов
    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    
    // 4. Отзыв старых токенов
    revokeAllToken(user);
    
    // 5. Сохранение новых токенов
    saveUserToken(accessToken, refreshToken, user);
    
    return new AuthenticationResponseDto(accessToken, refreshToken);
}
```

**Защита от race condition:**
- `@Transactional` — гарантирует атомарность
- `findByUsernameWithLock` — пессимистическая блокировка FOR UPDATE
- Блокирует пользователя до завершения транзакции
- Предотвращает одновременные логины одного пользователя

**Почему отзываем старые токены:**
- Безопасность: при новом логине старые токены становятся недействительными
- Предотвращение использования украденных токенов

#### Обновление токена (Refresh Token)
```java
@Transactional
public ResponseEntity<AuthenticationResponseDto> refreshToken(
        HttpServletRequest request,
        HttpServletResponse response) {
    
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    String token = authorizationHeader.substring(7);
    String username = jwtService.extractUsername(token);
    
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    
    if (jwtService.isValidRefresh(token, user)) {
        // Генерация новых токенов
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // Отзыв старых и сохранение новых
        revokeAllToken(user);
        saveUserToken(accessToken, refreshToken, user);
        
        return new ResponseEntity<>(
            new AuthenticationResponseDto(accessToken, refreshToken), 
            HttpStatus.OK
        );
    }
    
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}
```

**Refresh Token Flow:**
1. Клиент отправляет refresh token
2. Сервер валидирует refresh token
3. Если валиден → генерирует новые access и refresh токены
4. Старые токены отзываются
5. Новые токены возвращаются клиенту

**Почему нужен Refresh Token:**
- Access token имеет короткий срок жизни (10 часов)
- Refresh token имеет длинный срок жизни (7 дней)
- Клиент может обновить access token без повторного логина
- Если refresh token скомпрометирован, его можно отозвать

---

### 6. CustomAccessDeniedHandler.java — Обработка отказа в доступе

```java
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {
        response.setStatus(403);
    }
}
```

**Когда вызывается:**
- Пользователь аутентифицирован, но не имеет требуемых прав
- Например: USER пытается зайти на `/admin/**`, но требуется роль ADMIN

**HTTP 403 Forbidden** — "Я знаю, кто вы, но вам запрещено это делать"

---

### 7. CustomLogoutHandler.java — Обработка выхода

```java
public class CustomLogoutHandler implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        
        Token tokenEntity = tokenRepository
            .findFirstByAccessTokenAndLoggedOutFalse(token)
            .orElse(null);
        
        if (tokenEntity != null) {
            tokenEntity.setLoggedOut(true);  // Помечаем токен как недействительный
            tokenRepository.save(tokenEntity);
        }
    }
}
```

**Что делает:**
- Извлекает токен из запроса
- Находит токен в БД
- Помечает как `loggedOut = true`
- Токен больше не будет валидным при последующих запросах

**Почему важно:**
- Позволяет отозвать токен при выходе
- Защита от использования украденных токенов
- Можно реализовать "выход со всех устройств"

---

## JWT токены и их валидация

### Структура JWT в нашем приложении

#### Access Token
```json
{
  "sub": "username",
  "iat": 1234567890,
  "exp": 1234601490,
  "jti": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Срок жизни:** 10 часов
**Использование:** Передается в каждом запросе для доступа к защищенным ресурсам

#### Refresh Token
```json
{
  "sub": "username",
  "iat": 1234567890,
  "exp": 1234770690,
  "jti": "550e8400-e29b-41d4-a716-446655440001"
}
```

**Срок жизни:** 7 дней
**Использование:** Используется только для обновления access token

### Процесс валидации токена

```
1. Извлечение токена из заголовка
   ↓
2. Проверка формата (Bearer <token>)
   ↓
3. Парсинг токена (декодирование base64)
   ↓
4. Проверка подписи (HMAC-SHA256)
   ↓
5. Проверка срока действия (exp claim)
   ↓
6. Проверка в БД (активен ли токен, не отозван ли)
   ↓
7. Извлечение username (sub claim)
   ↓
8. Загрузка UserDetails
   ↓
9. Сравнение username
   ↓
10. Создание Authentication объекта
    ↓
11. Установка в SecurityContext
```

### Хранение токенов в БД

**Таблица `token_table`:**
- `id` — первичный ключ
- `access_token` — access token (текст)
- `refresh_token` — refresh token (текст)
- `is_logged_out` — флаг отзыва токена
- `user_id` — связь с пользователем

**Почему храним токены:**
- Возможность отзыва токенов (logout)
- Защита от повторного использования отозванных токенов
- Аудит (можно отследить активные сессии)

**Альтернативный подход (без БД):**
- Хранить только blacklist отозванных токенов
- Использовать Redis для быстрого доступа
- Но в нашем случае храним все токены для простоты

---

## Безопасность и защита от атак

### 1. Защита паролей

#### BCrypt хеширование
```java
passwordEncoder.encode("password123")
// Результат: "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
```

**Преимущества:**
- Односторонняя функция (невозможно восстановить пароль)
- Встроенная соль (защита от rainbow tables)
- Медленный алгоритм (защита от brute-force)
- Адаптивный (можно увеличить сложность)

**Проверка пароля:**
```java
passwordEncoder.matches("password123", hashedPassword)
// Сравнивает пароль с хешем
```

### 2. Защита от CSRF

**CSRF (Cross-Site Request Forgery)** — атака, когда злоумышленник заставляет пользователя выполнить действие на сайте, где он аутентифицирован.

**Пример атаки:**
```html
<!-- Злоумышленник создает форму на своем сайте -->
<form action="https://bank.com/transfer" method="POST">
    <input name="to" value="attacker_account">
    <input name="amount" value="1000">
</form>
<script>document.forms[0].submit();</script>
```

**Защита:**
- В stateful приложениях: CSRF токен в сессии
- В stateless приложениях (JWT): CSRF не нужен, т.к. токен в заголовке, не в cookie

**В нашем приложении:**
```java
http.csrf(AbstractHttpConfigurer::disable)
```

**Почему безопасно:**
- JWT токен передается в заголовке `Authorization`
- Браузер не отправляет заголовки автоматически при cross-origin запросах
- Злоумышленник не может прочитать токен из-за Same-Origin Policy

### 3. Защита от XSS

**XSS (Cross-Site Scripting)** — внедрение вредоносного JavaScript кода.

**Защита:**
- Валидация и санитизация входных данных
- Content Security Policy (CSP)
- HttpOnly cookies (но мы не используем cookies)

**В нашем приложении:**
- JWT токены хранятся на клиенте (localStorage/sessionStorage)
- Необходимо защищать от XSS на клиенте

### 4. Защита от SQL Injection

**SQL Injection** — внедрение SQL кода через входные данные.

**Защита:**
- Использование JPA/Hibernate (параметризованные запросы)
- Валидация входных данных

**В нашем приложении:**
```java
// Безопасно: параметризованный запрос
userRepository.findByUsername(username)

// JPA автоматически использует prepared statements
```

### 5. Защита от Race Conditions

**Race Condition** — ситуация, когда результат зависит от порядка выполнения операций.

**Пример проблемы:**
```java
// Поток 1: логин пользователя
User user = userRepository.findByUsername("user1");
revokeAllToken(user);
saveUserToken(newToken, user);

// Поток 2: одновременно логин того же пользователя
User user = userRepository.findByUsername("user1");
revokeAllToken(user);  // Может отозвать токен, который только что создал поток 1
saveUserToken(newToken, user);
```

**Защита в нашем приложении:**
```java
@Transactional
public AuthenticationResponseDto authenticate(...) {
    // Пессимистическая блокировка FOR UPDATE
    User user = userRepository.findByUsernameWithLock(username);
    // Пользователь заблокирован до завершения транзакции
    revokeAllToken(user);
    saveUserToken(accessToken, refreshToken, user);
}
```

**FOR UPDATE блокировка:**
- Блокирует строку в БД до завершения транзакции
- Другие транзакции ждут разблокировки
- Гарантирует атомарность операций

### 6. Защита токенов

#### От кражи токенов
- Использование HTTPS (обязательно в production)
- Хранение токенов в безопасном месте на клиенте
- Короткий срок жизни access token

#### От повторного использования
- Проверка токена в БД при каждой валидации
- Отзыв токенов при logout
- Отзыв всех токенов при новом логине

#### От подделки
- HMAC-SHA256 подпись (невозможно подделать без секретного ключа)
- Секретный ключ хранится только на сервере

---

## Заключение

### Ключевые моменты

1. **Spring Security работает через Filter Chain** — каждый запрос проходит через цепочку фильтров
2. **JWT токены обеспечивают stateless аутентификацию** — не нужны сессии
3. **SecurityContext хранит Authentication** — доступен в любом месте приложения
4. **UserDetailsService загружает пользователей** — связь между Spring Security и нашей БД
5. **PasswordEncoder хеширует пароли** — BCrypt обеспечивает безопасность
6. **Authorization проверяет права доступа** — на основе ролей из GrantedAuthority

### Архитектура безопасности в нашем приложении

```
HTTP Request
    ↓
SecurityFilterChain
    ↓
CORS Filter
    ↓
JwtFilter (кастомный)
    ├─ Извлечение токена
    ├─ Валидация токена
    ├─ Загрузка UserDetails
    └─ Установка Authentication в SecurityContext
    ↓
Authorization Filter
    ├─ Проверка правил доступа
    └─ Сравнение ролей
    ↓
Controller (если доступ разрешен)
    ↓
Response
```

### Рекомендации для production

1. **Секретный ключ JWT:**
   - Использовать переменные окружения
   - Использовать секретный менеджер (AWS Secrets Manager, HashiCorp Vault)

2. **CORS:**
   - Указать конкретные origin вместо "*"
   - Не использовать `allowCredentials(true)` с `allowedOriginPatterns("*")`

3. **HTTPS:**
   - Обязательно использовать HTTPS в production
   - JWT токены передаются в открытом виде без HTTPS

4. **Срок жизни токенов:**
   - Access token: 15-30 минут (вместо 10 часов)
   - Refresh token: 7 дней (можно оставить)

5. **Rate Limiting:**
   - Ограничить количество попыток логина
   - Защита от brute-force атак

6. **Мониторинг:**
   - Логировать неудачные попытки аутентификации
   - Отслеживать подозрительную активность

---

## Дополнительные ресурсы

- [Официальная документация Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/) — инструмент для работы с JWT
- [OWASP Top 10](https://owasp.org/www-project-top-ten/) — основные уязвимости веб-приложений

---

*Документ создан на основе анализа кода приложения IS_LAB1*





