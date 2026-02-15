package com.acme.bookmanagement.exception;

public class DuplicateAuthorException extends RuntimeException {
    public DuplicateAuthorException(String name) {
        super("Author already exists with name: " + name);
    }
}
