package com.acme.bookmanagement.service;

import com.acme.bookmanagement.exception.DuplicateAuthorException;
import com.acme.bookmanagement.model.Author;
import com.acme.bookmanagement.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthorServiceTest {
    private final AuthorRepository authorRepository = mock(AuthorRepository.class);
    private final AuthorService authorService = new AuthorService(authorRepository);
    private final Author author = new Author(1L, "Test Author", null);

    @Test
    void testFindAll() {
        when(authorRepository.findAll()).thenReturn(List.of(author));
        
        List<Author> result = authorService.findAll();
        
        assertEquals(1, result.size());
        assertEquals("Test Author", result.get(0).getName());
    }

    @Test
    void testFindByName() {
        when(authorRepository.findByNameContainingIgnoreCase("Test")).thenReturn(List.of(author));
        
        List<Author> result = authorService.findByName("Test");
        
        assertEquals(1, result.size());
        assertEquals("Test Author", result.get(0).getName());
    }

    @Test
    void testFindByNameTrimsInput() {
        when(authorRepository.findByNameContainingIgnoreCase("Test")).thenReturn(List.of(author));
        
        List<Author> result = authorService.findByName("  Test  ");
        
        assertEquals(1, result.size());
        verify(authorRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void testFindByNameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            authorService.findByName(""));
    }

    @Test
    void testCreate() {
        when(authorRepository.findByNameIgnoreCase("Test Author")).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        
        Author result = authorService.create("Test Author");
        
        assertNotNull(result);
        assertEquals("Test Author", result.getName());
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testCreateDuplicateAuthor() {
        when(authorRepository.findByNameIgnoreCase("Test Author")).thenReturn(Optional.of(author));
        
        assertThrows(DuplicateAuthorException.class, () -> authorService.create("Test Author"));
        verify(authorRepository, never()).save(any(Author.class));
    }
}
