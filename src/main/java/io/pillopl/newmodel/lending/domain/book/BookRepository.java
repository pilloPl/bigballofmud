package io.pillopl.newmodel.lending.domain.book;

import io.pillopl.newmodel.catalogue.BookId;

import java.util.Optional;

public interface BookRepository {

    Optional<Book> findById(BookId bookId);

    void save(Book book);

}


