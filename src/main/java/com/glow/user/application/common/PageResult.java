package com.glow.user.application.common;

import java.util.List;

public record PageResult<T>(
    List<T> result,
    int nextPage,
    boolean lastPage) {

    public int size() {
        return result.size();
    }
}