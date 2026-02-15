package com.acme.bookmanagement.repository;

import com.acme.bookmanagement.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByAuthorId(Long authorId, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.author.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Book> findByAuthorNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    Page<Book> findByPublishedDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Book> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) = LOWER(:title) AND b.author.id = :authorId")
    Optional<Book> findByTitleAndAuthorId(@Param("title") String title, @Param("authorId") Long authorId);
}

