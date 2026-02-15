package com.acme.bookmanagement.repository;

import com.acme.bookmanagement.model.Author;
import com.acme.bookmanagement.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private com.acme.bookmanagement.repository.AuthorRepository authorRepository;

    @Test
    void testSaveAndFindBook() {
        Author author = authorRepository.save(new Author(null, "Test Author", null));
        Book book = new Book(null, "Test Book", author, LocalDate.of(2021, 2, 3));
        Book saved = bookRepository.save(book);

        assertNotNull(saved.getId());
        assertEquals("Test Book", saved.getTitle());
    }

    @Test
    void testFindByAuthorId() {
        Author author = authorRepository.save(new Author(null, "Author 1", null));
        bookRepository.save(new Book(null, "Book 1", author, LocalDate.now()));
        bookRepository.save(new Book(null, "Book 2", author, LocalDate.now()));

        Page<Book> result = bookRepository.findByAuthorId(author.getId(), PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testFindByAuthorNameIgnoreCase() {
        Author author = authorRepository.save(new Author(null, "John Doe", null));
        bookRepository.save(new Book(null, "Book 1", author, LocalDate.now()));

        Page<Book> result = bookRepository.findByAuthorNameContainingIgnoreCase("john", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testFindByAuthorNamePartialMatch() {
        Author author = authorRepository.save(new Author(null, "John Doe", null));
        bookRepository.save(new Book(null, "Book 1", author, LocalDate.now()));

        Page<Book> result = bookRepository.findByAuthorNameContainingIgnoreCase("doe", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testFindByPublishedDateBetween() {
        Author author = authorRepository.save(new Author(null, "UniqueAuthor123", null));
        Book book1 = bookRepository.save(new Book(null, "Book 1", author, LocalDate.of(2021, 6, 15)));
        bookRepository.save(new Book(null, "Book 2", author, LocalDate.of(2022, 6, 15)));
        bookRepository.save(new Book(null, "Book 3", author, LocalDate.of(2020, 6, 15)));

        Page<Book> result = bookRepository.findByPublishedDateBetween(
            LocalDate.of(2021, 1, 1), 
            LocalDate.of(2021, 12, 31), 
            PageRequest.of(0, 10)
        );

        assertTrue(result.getTotalElements() >= 1);
        assertTrue(result.getContent().stream().anyMatch(b -> b.getId().equals(book1.getId())));
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        Author author = authorRepository.save(new Author(null, "UniqueAuthor456", null));
        Book shadow1 = bookRepository.save(new Book(null, "The Shadow XYZ123", author, LocalDate.now()));
        Book shadow2 = bookRepository.save(new Book(null, "Shadow Warrior XYZ123", author, LocalDate.now()));
        bookRepository.save(new Book(null, "Different Book XYZ123", author, LocalDate.now()));

        Page<Book> result = bookRepository.findByTitleContainingIgnoreCase("XYZ123", PageRequest.of(0, 100));

        long count = result.getContent().stream()
            .filter(b -> b.getTitle().contains("XYZ123"))
            .count();
        assertEquals(3, count);
    }
}
