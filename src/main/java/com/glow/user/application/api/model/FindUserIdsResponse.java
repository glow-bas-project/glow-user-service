package com.glow.user.application.api.model;

import java.util.List;

public record FindUserIdsResponse(
    List<String> ids,
    int nextPage,
    boolean hasNext
) {
}