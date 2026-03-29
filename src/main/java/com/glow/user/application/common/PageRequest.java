package com.glow.user.application.common;

public record PageRequest(
    int size,
    int page) {
}