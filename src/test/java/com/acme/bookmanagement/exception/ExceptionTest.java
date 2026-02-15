package com.acme.bookmanagement.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testDuplicateAuthorExceptionMessage() {
        DuplicateAuthorException exception = new DuplicateAuthorException("John Doe");
        assertEquals("Author already exists with name: John Doe", exception.getMessage());
    }

    @Test
    void testDuplicateBookExceptionMessage() {
        DuplicateBookException exception = new DuplicateBookException("1984", "George Orwell");
        assertEquals("Book already exists with title '1984' by author 'George Orwell'", exception.getMessage());
    }

    @Test
    void testBookNotFoundExceptionMessage() {
        BookNotFoundException exception = new BookNotFoundException(123L);
        assertEquals("Book not found with id: 123", exception.getMessage());
    }

    @Test
    void testAuthorNotFoundExceptionMessage() {
        AuthorNotFoundException exception = new AuthorNotFoundException(456L);
        assertEquals("Author not found with id: 456", exception.getMessage());
    }
}
