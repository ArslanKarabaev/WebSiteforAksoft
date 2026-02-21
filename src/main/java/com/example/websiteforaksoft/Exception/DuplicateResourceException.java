package com.example.websiteforaksoft.Exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s с %s = '%s' уже существует", resourceName, fieldName, fieldValue));
    }
}