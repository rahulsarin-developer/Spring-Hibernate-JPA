package com.project.spring_hibernate_jpa.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}
