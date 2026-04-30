package dev.github.sterio0o.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private final String secret;
    private final long tokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long tokenExpiration
    ) {
        this.secret = secret;
        this.tokenExpiration = tokenExpiration;
    }

    public String generateToken(String userId, String email, List<String> roles) {
        return Jwts.builder()
                .subject(userId)           // Кто
                .claim("email", email)  // Доп. инфа
                .claim("roles", roles)  // Роли доступа
                .issuedAt(new Date())      // Когда выдан
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration)) // Время окончания жизни токена
                .signWith(getSigningKey()) // Подпись
                .compact();                // Сбор в единую строку
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Берем ту же подпись
                .build()
                .parseSignedClaims(token)    // Проверяем подпись и срок годности
                .getPayload();               // Возвращаем данные
    }

    // Получить юзера из токена
    public String extractUserId(String token) {
        return validateToken(token).getSubject();
    }

    // Получить роли из токена
    public List<String> extractRoles(String token) {
        return validateToken(token).get("roles", List.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = validateToken(token);
            return !claims.getExpiration().before(new Date()); // Истек срок годности или нет
        } catch (Exception e) {
            return false;   // Любая ошибка значит что токен невалиден
        }
    }

    // Создает специальный ключ для подписи
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}