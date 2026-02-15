package com.acme.bookmanagement.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookPageTest {

    @Test
    void testBookPageCreation() {
        Author author = new Author(1L, "Test Author", null);
        Book book = new Book(1L, "Test Book", author, LocalDate.now());
        
        BookPage bookPage = new BookPage(List.of(book), 1, 1, 0, 10);
        
        assertEquals(1, bookPage.content().size());
        assertEquals(1, bookPage.totalElements());
        assertEquals(1, bookPage.totalPages());
        assertEquals(0, bookPage.number());
        assertEquals(10, bookPage.size());
    }

    @Test
    void testEmptyBookPage() {
        BookPage bookPage = new BookPage(List.of(), 0, 0, 0, 10);
        
        assertTrue(bookPage.content().isEmpty());
        assertEquals(0, bookPage.totalElements());
    }
}
