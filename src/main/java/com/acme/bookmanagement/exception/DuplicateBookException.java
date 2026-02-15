package com.acme.bookmanagement.exception;

public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String title, String authorName) {
        super("Book already exists with title '" + title + "' by author '" + authorName + "'");
    }
}
