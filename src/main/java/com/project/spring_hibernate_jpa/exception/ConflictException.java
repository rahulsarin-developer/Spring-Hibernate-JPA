package com.project.spring_hibernate_jpa.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message)
    {
        super(message);
    }
}