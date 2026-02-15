package com.acme.bookmanagement.model;

import java.util.List;

public record BookPage(List<Book> content, int totalElements, int totalPages, int number, int size) {
}
