package com.glow.user.domain.shared;

import java.util.List;

public record PageResult<T>(
    List<T> result,
    int nextPage,
    boolean lastPage) {

    public int size() {
        return result.size();
    }
}