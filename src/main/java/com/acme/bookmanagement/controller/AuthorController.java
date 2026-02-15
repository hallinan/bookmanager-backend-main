package com.acme.bookmanagement.controller;

import com.acme.bookmanagement.model.Author;
import com.acme.bookmanagement.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @QueryMapping
    public List<Author> findAllAuthors() {
        return authorService.findAll();
    }

    @QueryMapping
    public List<Author> findAuthorsByName(@Argument String name) {
        return authorService.findByName(name);
    }

    @MutationMapping
    public Author createAuthor(@Argument String name) {
        return authorService.create(name);
    }
}
