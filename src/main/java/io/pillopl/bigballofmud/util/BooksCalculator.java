package io.pillopl.bigballofmud.util;

import io.pillopl.bigballofmud.entities.BookEntity;

import java.time.Instant;
import java.util.Set;

public class BooksCalculator {

    public static long countOnHold(Set<BookEntity> books) {
        return
                books
                        .stream()
                        .filter(book -> book.getLendingState() == BookEntity.BookLendingState.OnHold && book.getOnHoldFrom() != null && book.getCollectedFrom() == null)
                        .count();
    }

    public static long collectedTill(Set<BookEntity> books, Instant tillWhen) {
        return
                books
                        .stream()
                        .filter(book -> book.getLendingState() == BookEntity.BookLendingState.Collected && book.getCollectedFrom() != null && book.getCollectedTill().isBefore(tillWhen))
                        .count();
    }
}
