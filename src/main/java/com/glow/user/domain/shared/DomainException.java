package com.glow.user.domain.shared;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}