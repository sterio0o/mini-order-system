package dev.github.sterio0o.userservice.controller;

import dev.github.sterio0o.common.security.JwtService;
import dev.github.sterio0o.userservice.model.dto.request.AuthRequestDto;
import dev.github.sterio0o.userservice.model.dto.request.RegisterRequestDto;
import dev.github.sterio0o.userservice.model.dto.response.AuthResponseDto;
import dev.github.sterio0o.userservice.model.entity.User;
import dev.github.sterio0o.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto requestDto) {
        User user = userService.register(requestDto);
        String token = jwtService.generateToken(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getRoles()
        );

        return ResponseEntity.ok(new AuthResponseDto(token, user.getEmail(), user.getRoles()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto requestDto) {
        User user = userService.authenticate(requestDto.email(), requestDto.password());
        String token = jwtService.generateToken(
                String.valueOf(user.getId()),
                user.getEmail(),
                user.getRoles()
        );

        return ResponseEntity.ok(new AuthResponseDto(token, user.getEmail(), user.getRoles()));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        boolean isValid = jwtService.isTokenValid(token);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
}
