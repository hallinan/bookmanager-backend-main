package com.acme.bookmanagement.controller;

import com.acme.bookmanagement.model.Author;
import com.acme.bookmanagement.model.Book;
import com.acme.bookmanagement.model.BookPage;
import com.acme.bookmanagement.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private final Author author = new Author(1L, "Test Author", null);
    private final Book book = new Book(1L, "Test Book", author, LocalDate.of(2021, 2, 3));

    @Test
    void shouldFindAllBooks() {
        BookPage bookPage = new BookPage(List.of(book), 1, 1, 0, 10);
        when(bookService.findAll(any(), any())).thenReturn(bookPage);

        BookPage result = bookController.findAllBooks(0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(bookService).findAll(0, 10);
    }

    @Test
    void shouldFindBooksByAuthorId() {
        BookPage bookPage = new BookPage(List.of(book), 1, 1, 0, 10);
        when(bookService.findByAuthorId(eq(1L), any(), any())).thenReturn(bookPage);

        BookPage result = bookController.findBooksByAuthorId(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(bookService).findByAuthorId(1L, 0, 10);
    }

    @Test
    void shouldFindBooksByAuthorName() {
        BookPage bookPage = new BookPage(List.of(book), 1, 1, 0, 10);
        when(bookService.findByAuthorName(eq("Test Author"), any(), any())).thenReturn(bookPage);

        BookPage result = bookController.findBooksByAuthorName("Test Author", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(bookService).findByAuthorName("Test Author", 0, 10);
    }

    @Test
    void shouldFindBooksByDateRange() {
        BookPage bookPage = new BookPage(List.of(book), 1, 1, 0, 10);
        when(bookService.findByDateRange(eq("2021-01-01"), eq("2021-12-31"), any(), any())).thenReturn(bookPage);

        BookPage result = bookController.findBooksByDateRange("2021-01-01", "2021-12-31", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(bookService).findByDateRange("2021-01-01", "2021-12-31", 0, 10);
    }

    @Test
    void shouldFindBooksByTitle() {
        BookPage bookPage = new BookPage(List.of(book), 1, 1, 0, 10);
        when(bookService.findByTitle(eq("Test"), any(), any())).thenReturn(bookPage);

        BookPage result = bookController.findBooksByTitle("Test", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(bookService).findByTitle("Test", 0, 10);
    }

    @Test
    void shouldCreateBook() {
        when(bookService.create(eq("New Book"), eq(1L), eq("2021-02-03"))).thenReturn(book);

        Book result = bookController.createBook("New Book", 1L, "2021-02-03");

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookService).create("New Book", 1L, "2021-02-03");
    }

    @Test
    void shouldUpdateBook() {
        when(bookService.update(eq(1L), eq("Updated"), eq(1L), eq("2021-02-03"))).thenReturn(book);

        Book result = bookController.updateBook(1L, "Updated", 1L, "2021-02-03");

        assertNotNull(result);
        verify(bookService).update(1L, "Updated", 1L, "2021-02-03");
    }

    @Test
    void shouldDeleteBook() {
        when(bookService.delete(1L)).thenReturn(1L);

        Long result = bookController.deleteBook(1L);

        assertEquals(1L, result);
        verify(bookService).delete(1L);
    }
}
