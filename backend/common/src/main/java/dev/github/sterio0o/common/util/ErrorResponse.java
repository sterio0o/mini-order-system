package dev.github.sterio0o.common.util;

public record ErrorResponse(
        int status,
        String message
) {
}
