package dev.github.sterio0o.userservice.model.dto.response;

import java.util.List;

public record AuthResponseDto(
        String token,
        String email,
        List<String> roles
) {
}
