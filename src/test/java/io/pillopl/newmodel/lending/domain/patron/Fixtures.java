package io.pillopl.newmodel.lending.domain.patron;


import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;
import io.pillopl.newmodel.lending.domain.book.BookType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class Fixtures {

    public static Patron aRegularPatron() {
        return new Patron(PatronType.Regular, new PatronId(UUID.randomUUID()), new Holds(new HashSet<>()), new OverdueCollectedBooks(new HashSet<>()));
    }

    public static Patron aResearcherPatron() {
        return new Patron(PatronType.Researcher, new PatronId(UUID.randomUUID()), new Holds(new HashSet<>()), new OverdueCollectedBooks(new HashSet<>()));
    }

    public static Patron aResearcherPatronWithTwoOverdueBooks() {
        Set<BookId> overdueBooks = new HashSet<>();
        overdueBooks.add(anyBookId());
        overdueBooks.add(anyBookId());
        return new Patron(PatronType.Researcher, new PatronId(UUID.randomUUID()), new Holds(new HashSet<>()), new OverdueCollectedBooks(overdueBooks));
    }

    public static BookId anyBookId() {
        return new BookId(UUID.randomUUID());
    }

    public static AvailableBook restrictedBook() {
        return new AvailableBook(anyBookId(), BookType.Restricted);
    }

    public static AvailableBook circulatingBook() {
        return new AvailableBook(anyBookId(), BookType.Circulating);
    }
}

