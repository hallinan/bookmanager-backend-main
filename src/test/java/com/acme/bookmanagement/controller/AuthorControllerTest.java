package com.acme.bookmanagement.controller;

import com.acme.bookmanagement.model.Author;
import com.acme.bookmanagement.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    private final Author author = new Author(1L, "Test Author", null);

    @Test
    void shouldFindAllAuthors() {
        when(authorService.findAll()).thenReturn(List.of(author));

        List<Author> result = authorController.findAllAuthors();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Author", result.get(0).getName());
        verify(authorService).findAll();
    }

    @Test
    void shouldFindAuthorsByName() {
        when(authorService.findByName(eq("Test"))).thenReturn(List.of(author));

        List<Author> result = authorController.findAuthorsByName("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Author", result.get(0).getName());
        verify(authorService).findByName("Test");
    }

    @Test
    void shouldCreateAuthor() {
        when(authorService.create(eq("New Author"))).thenReturn(author);

        Author result = authorController.createAuthor("New Author");

        assertNotNull(result);
        assertEquals("Test Author", result.getName());
        verify(authorService).create("New Author");
    }
}
