package com.glow.user.domain.model;

public record Address(
    String address,
    String city,
    String country,
    Double longitude,
    Double latitude) {
}