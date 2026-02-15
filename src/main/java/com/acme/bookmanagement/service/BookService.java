package com.acme.bookmanagement.service;

import com.acme.bookmanagement.exception.AuthorNotFoundException;
import com.acme.bookmanagement.exception.BookNotFoundException;
import com.acme.bookmanagement.exception.DuplicateBookException;
import com.acme.bookmanagement.model.Author;
import com.acme.bookmanagement.model.Book;
import com.acme.bookmanagement.model.BookPage;
import com.acme.bookmanagement.repository.AuthorRepository;
import com.acme.bookmanagement.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.acme.bookmanagement.util.ValidationUtil.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookPage findAll(Integer page, Integer size) {
        return toBookPage(bookRepository.findAll(PageRequest.of(validatePage(page), validateSize(size))));
    }

    public BookPage findByAuthorId(Long authorId, Integer page, Integer size) {
        validatePositiveId(authorId, "Author ID");
        PageRequest pageRequest = PageRequest.of(validatePage(page), validateSize(size));
        return toBookPage(bookRepository.findByAuthorId(authorId, pageRequest));
    }

    public BookPage findByAuthorName(String authorName, Integer page, Integer size) {
        validateNotEmpty(authorName, "Author name");
        PageRequest pageRequest = PageRequest.of(validatePage(page), validateSize(size));
        return toBookPage(bookRepository.findByAuthorNameContainingIgnoreCase(authorName.trim(), pageRequest));
    }

    public BookPage findByDateRange(String startDate, String endDate, Integer page, Integer size) {
        validateNotEmpty(startDate, "Start date");
        LocalDate start = parseDate(startDate, "start date");
        LocalDate end = (endDate == null || endDate.trim().isEmpty()) ? LocalDate.now() : parseDate(endDate, "end date");
        validateDateRange(start, end);
        PageRequest pageRequest = PageRequest.of(validatePage(page), validateSize(size));
        return toBookPage(bookRepository.findByPublishedDateBetween(start, end, pageRequest));
    }

    public BookPage findByTitle(String title, Integer page, Integer size) {
        validateNotEmpty(title, "Title");
        PageRequest pageRequest = PageRequest.of(validatePage(page), validateSize(size));
        return toBookPage(bookRepository.findByTitleContainingIgnoreCase(title.trim(), pageRequest));
    }

    @Transactional
    public Book create(String title, Long authorId, String publishedDate) {
        validateNotEmpty(title, "Title");
        validatePositiveId(authorId, "Author ID");
        validateNotEmpty(publishedDate, "Published date");
        
        String trimmedTitle = title.trim();
        Author author = getAuthor(authorId);
        checkDuplicateBook(trimmedTitle, authorId, author.getName());
        
        return bookRepository.save(new Book(null, trimmedTitle, author, parseDate(publishedDate, "published date")));
    }

    @Transactional
    public Book update(Long id, String title, Long authorId, String publishedDate) {
        validatePositiveId(id, "Book ID");
        if (title != null) validateNotEmpty(title, "Title");
        if (authorId != null) validatePositiveId(authorId, "Author ID");
        
        Book book = getBook(id);
        String finalTitle = title != null ? title.trim() : book.getTitle();
        Long finalAuthorId = authorId != null ? authorId : book.getAuthor().getId();
        
        if (title != null || authorId != null) {
            checkDuplicateBookForUpdate(finalTitle, finalAuthorId, id);
        }
        
        if (title != null) book.setTitle(finalTitle);
        if (authorId != null) book.setAuthor(getAuthor(authorId));
        if (publishedDate != null) book.setPublishedDate(parseDate(publishedDate, "published date"));
        
        return bookRepository.save(book);
    }

    @Transactional
    public Long delete(Long id) {
        validatePositiveId(id, "Book ID");
        getBook(id);
        bookRepository.deleteById(id);
        return id;
    }

    private Author getAuthor(Long authorId) {
        return authorRepository.findById(authorId)
            .orElseThrow(() -> new AuthorNotFoundException(authorId));
    }

    private Book getBook(Long bookId) {
        return bookRepository.findById(bookId)
            .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    private void checkDuplicateBook(String title, Long authorId, String authorName) {
        if (bookRepository.findByTitleAndAuthorId(title, authorId).isPresent()) {
            throw new DuplicateBookException(title, authorName);
        }
    }

    private void checkDuplicateBookForUpdate(String title, Long authorId, Long excludeBookId) {
        bookRepository.findByTitleAndAuthorId(title, authorId)
            .filter(b -> !b.getId().equals(excludeBookId))
            .ifPresent(b -> {
                throw new DuplicateBookException(title, b.getAuthor().getName());
            });
    }

    private BookPage toBookPage(Page<Book> page) {
        return new BookPage(page.getContent(), (int) page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }
}