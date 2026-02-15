package com.acme.bookmanagement;

import com.acme.bookmanagement.model.Author;
import com.acme.bookmanagement.model.Book;
import com.acme.bookmanagement.repository.AuthorRepository;
import com.acme.bookmanagement.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDate;

@SpringBootApplication
public class BookManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookManagementApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(BookRepository bookRepository, AuthorRepository authorRepository) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode data = mapper.readTree(new ClassPathResource("data.json").getInputStream());
            
            JsonNode authorsNode = data.get("authors");
            Author[] authors = new Author[authorsNode.size()];
            for (int i = 0; i < authorsNode.size(); i++) {
                authors[i] = authorRepository.save(new Author(null, authorsNode.get(i).asText(), null));
            }
            
            JsonNode bookTitlesNode = data.get("bookTitles");
            for (int i = 0; i < 1000; i++) {
                Author author = authors[i % authors.length];
                String title = bookTitlesNode.get(i % bookTitlesNode.size()).asText() + " " + (i / bookTitlesNode.size() + 1);
                LocalDate publishedDate = LocalDate.of(1900 + (i % 124), (i % 12) + 1, (i % 28) + 1);
                bookRepository.save(new Book(null, title, author, publishedDate));
            }
        };
    }

}
