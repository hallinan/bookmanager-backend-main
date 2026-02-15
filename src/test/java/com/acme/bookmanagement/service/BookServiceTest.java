package com.acme.bookmanagement.service;

import com.acme.bookmanagement.exception.AuthorNotFoundException;
import com.acme.bookmanagement.exception.BookNotFoundException;
import com.acme.bookmanagement.model.Author;
import com.acme.bookmanagement.model.Book;
import com.acme.bookmanagement.model.BookPage;
import com.acme.bookmanagement.repository.AuthorRepository;
import com.acme.bookmanagement.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookServiceTest {
    private final BookRepository bookRepository = mock(BookRepository.class);
    private final AuthorRepository authorRepository = mock(AuthorRepository.class);
    private final BookService bookService = new BookService(bookRepository, authorRepository);
    private final Author author = new Author(1L, "Test Author", null);
    private final Book book = new Book(1L, "title-1", author, LocalDate.of(2021, 2, 3));

    @Test
    void testFindAll() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(page);
        
        BookPage result = bookService.findAll(0, 10);
        
        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
    }

    @Test
    void testFindByAuthorId() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findByAuthorId(eq(1L), any(PageRequest.class))).thenReturn(page);
        
        BookPage result = bookService.findByAuthorId(1L, 0, 10);
        
        assertEquals(1, result.content().size());
        assertEquals(book, result.content().get(0));
    }

    @Test
    void testFindByAuthorName() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findByAuthorNameContainingIgnoreCase(eq("Test Author"), any(PageRequest.class))).thenReturn(page);
        
        BookPage result = bookService.findByAuthorName("Test Author", 0, 10);
        
        assertEquals(1, result.content().size());
    }

    @Test
    void testFindByDateRange() {
        LocalDate startDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 12, 31);
        Page<Book> page = new PageImpl<>(List.of(book));
        
        when(bookRepository.findByPublishedDateBetween(eq(startDate), eq(endDate), any(PageRequest.class))).thenReturn(page);
        
        BookPage result = bookService.findByDateRange("2021-01-01", "2021-12-31", 0, 10);
        
        assertEquals(1, result.content().size());
        assertEquals(book, result.content().get(0));
    }

    @Test
    void testFindByTitle() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findByTitleContainingIgnoreCase(eq("title"), any(PageRequest.class))).thenReturn(page);
        
        BookPage result = bookService.findByTitle("title", 0, 10);
        
        assertEquals(1, result.content().size());
        assertEquals(book, result.content().get(0));
    }

    @Test
    void testFindByTitleTrimsInput() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findByTitleContainingIgnoreCase(eq("title"), any(PageRequest.class))).thenReturn(page);
        
        BookPage result = bookService.findByTitle("  title  ", 0, 10);
        
        assertEquals(1, result.content().size());
        verify(bookRepository).findByTitleContainingIgnoreCase("title", PageRequest.of(0, 10));
    }

    @Test
    void testFindByTitleEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            bookService.findByTitle("", 0, 10));
    }

    @Test
    void testCreate() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findByTitleAndAuthorId("title-1", 1L)).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        Book result = bookService.create("title-1", 1L, "2021-02-03");
        
        assertNotNull(result);
        assertEquals("title-1", result.getTitle());
    }

    @Test
    void testCreateTrimsTitle() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findByTitleAndAuthorId("title-1", 1L)).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Book result = bookService.create("  title-1  ", 1L, "2021-02-03");
        
        assertEquals("title-1", result.getTitle());
        verify(bookRepository).findByTitleAndAuthorId("title-1", 1L);
    }

    @Test
    void testCreateWithInvalidAuthor() {
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(AuthorNotFoundException.class, () -> 
            bookService.create("title", 999L, "2021-01-01"));
    }

    @Test
    void testCreateDuplicateBook() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findByTitleAndAuthorId("title-1", 1L)).thenReturn(Optional.of(book));
        
        assertThrows(com.acme.bookmanagement.exception.DuplicateBookException.class, () -> 
            bookService.create("title-1", 1L, "2021-02-03"));
    }

    @Test
    void testUpdate() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findByTitleAndAuthorId("new-title", 1L)).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        Book result = bookService.update(1L, "new-title", 1L, "2022-01-01");
        
        assertNotNull(result);
    }

    @Test
    void testUpdateEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> 
            bookService.update(1L, "  ", null, null));
    }

    @Test
    void testUpdateDuplicateBook() {
        Book existingBook = new Book(2L, "existing-title", author, LocalDate.of(2021, 1, 1));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByTitleAndAuthorId("existing-title", 1L))
            .thenReturn(Optional.of(existingBook));
        
        assertThrows(com.acme.bookmanagement.exception.DuplicateBookException.class, () -> 
            bookService.update(1L, "existing-title", null, null));
    }

    @Test
    void testUpdateWithInvalidBook() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(BookNotFoundException.class, () -> 
            bookService.update(999L, "title", 1L, "2021-01-01"));
    }

    @Test
    void testUpdateWithInvalidAuthor() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(AuthorNotFoundException.class, () -> 
            bookService.update(1L, null, 999L, null));
    }

    @Test
    void testDelete() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        
        Long result = bookService.delete(1L);
        
        assertEquals(1L, result);
        verify(bookRepository).deleteById(1L);
    }
}
