package com.glow.user.domain.shared;

public record PageRequest(
    int size,
    int page) {
}