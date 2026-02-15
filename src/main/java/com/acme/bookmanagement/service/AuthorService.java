package com.acme.bookmanagement.service;

import com.acme.bookmanagement.exception.DuplicateAuthorException;
import com.acme.bookmanagement.model.Author;
import com.acme.bookmanagement.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.acme.bookmanagement.util.ValidationUtil.validateNotEmpty;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {
    private final AuthorRepository authorRepository;

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public List<Author> findByName(String name) {
        validateNotEmpty(name, "Author name");
        return authorRepository.findByNameContainingIgnoreCase(name.trim());
    }

    @Transactional
    public Author create(String name) {
        validateNotEmpty(name, "Author name");
        String trimmedName = name.trim();
        if (authorRepository.findByNameIgnoreCase(trimmedName).isPresent()) {
            throw new DuplicateAuthorException(trimmedName);
        }
        return authorRepository.save(new Author(null, trimmedName, null));
    }
}
