package dev.github.sterio0o.userservice.model.dto.request;

public record RegisterRequestDto(
        String email,
        String password,
        String name
) {
}
