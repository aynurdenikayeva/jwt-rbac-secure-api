package com.aynur.payment.common.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}