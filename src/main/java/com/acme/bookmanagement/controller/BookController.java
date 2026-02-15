package com.acme.bookmanagement.controller;

import com.acme.bookmanagement.model.Book;
import com.acme.bookmanagement.model.BookPage;
import com.acme.bookmanagement.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @QueryMapping
    public BookPage findAllBooks(@Argument Integer page, @Argument Integer size) {
        return bookService.findAll(page, size);
    }

    @QueryMapping
    public BookPage findBooksByAuthorId(@Argument Long authorId, @Argument Integer page, @Argument Integer size) {
        return bookService.findByAuthorId(authorId, page, size);
    }

    @QueryMapping
    public BookPage findBooksByAuthorName(@Argument String authorName, @Argument Integer page, @Argument Integer size) {
        return bookService.findByAuthorName(authorName, page, size);
    }

    @QueryMapping
    public BookPage findBooksByDateRange(@Argument String startDate, @Argument String endDate, @Argument Integer page, @Argument Integer size) {
        return bookService.findByDateRange(startDate, endDate, page, size);
    }

    @QueryMapping
    public BookPage findBooksByTitle(@Argument String title, @Argument Integer page, @Argument Integer size) {
        return bookService.findByTitle(title, page, size);
    }

    @MutationMapping
    public Book createBook(@Argument String title, @Argument Long authorId, @Argument String publishedDate) {
        return bookService.create(title, authorId, publishedDate);
    }

    @MutationMapping
    public Book updateBook(@Argument Long id, @Argument String title, @Argument Long authorId, @Argument String publishedDate) {
        return bookService.update(id, title, authorId, publishedDate);
    }

    @MutationMapping
    public Long deleteBook(@Argument Long id) {
        return bookService.delete(id);
    }
}