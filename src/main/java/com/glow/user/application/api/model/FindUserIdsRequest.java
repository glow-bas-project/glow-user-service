package com.glow.user.application.api.model;

public record FindUserIdsRequest(
    int page,
    int size
) {
}