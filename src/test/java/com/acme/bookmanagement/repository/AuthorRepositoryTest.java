package com.acme.bookmanagement.repository;

import com.acme.bookmanagement.model.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testSaveAndFindAuthor() {
        Author author = new Author(null, "Test Author", null);
        Author saved = authorRepository.save(author);

        assertNotNull(saved.getId());
        assertEquals("Test Author", saved.getName());
    }

    @Test
    void testFindAll() {
        authorRepository.save(new Author(null, "Author 1", null));
        authorRepository.save(new Author(null, "Author 2", null));

        assertTrue(authorRepository.findAll().size() >= 2);
    }

    @Test
    void testFindByNameIgnoreCase() {
        authorRepository.save(new Author(null, "John Doe", null));

        assertTrue(authorRepository.findByNameIgnoreCase("john doe").isPresent());
        assertTrue(authorRepository.findByNameIgnoreCase("JOHN DOE").isPresent());
        assertFalse(authorRepository.findByNameIgnoreCase("Jane Doe").isPresent());
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        Author george = authorRepository.save(new Author(null, "UniqueGeorge Orwell", null));
        Author harper = authorRepository.save(new Author(null, "UniqueHarper Lee", null));
        Author mark = authorRepository.save(new Author(null, "UniqueMark Twain", null));

        var georgeResults = authorRepository.findByNameContainingIgnoreCase("uniquegeorge");
        assertEquals(1, georgeResults.size());
        assertEquals(george.getId(), georgeResults.get(0).getId());
        
        var orwellResults = authorRepository.findByNameContainingIgnoreCase("orwell");
        assertTrue(orwellResults.stream().anyMatch(a -> a.getId().equals(george.getId())));
    }
}
