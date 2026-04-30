package dev.github.sterio0o.userservice.service;

import dev.github.sterio0o.userservice.model.dto.request.RegisterRequestDto;
import dev.github.sterio0o.userservice.model.entity.User;
import dev.github.sterio0o.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(requestDto.email())
                .password(passwordEncoder.encode(requestDto.password()))
                .name(requestDto.name())
                .roles(List.of("USER"))
                .build();

        return userRepository.save(user);
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid authentification"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid authentification");
        }

        return user;
    }
}
