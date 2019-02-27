package io.pillopl.newmodel.lending.infrastructure;

import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.application.FindAvailableBook;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;
import io.pillopl.newmodel.lending.domain.book.Book;
import io.pillopl.newmodel.lending.domain.book.BookRepository;
import io.pillopl.newmodel.lending.domain.patron.events.BookPlacedOnHold;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryBookRepository implements FindAvailableBook, BookRepository {

    private final Map<BookId, Book> database = new HashMap<>();

    @Override
    public Optional<AvailableBook> find(BookId bookId) {
        return Optional.ofNullable(database.get(bookId)).flatMap(Book::toAvailableBook);
    }

    @Override
    public Optional<Book> findById(BookId bookId) {
        return Optional.ofNullable(database.get(bookId));
    }

    @Override
    public void save(Book book) {
        database.put(book.getBookId(), book);
    }

    @EventListener
    public void handle(BookPlacedOnHold bookPlacedOnHold) {
        database.get(bookPlacedOnHold.getBookId()).handle(bookPlacedOnHold);
    }
}
