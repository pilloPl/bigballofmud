package io.pillopl.newmodel.lending.domain.patron;


import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;
import io.pillopl.newmodel.lending.domain.book.BookType;

import java.util.HashSet;
import java.util.UUID;

import static io.pillopl.newmodel.lending.domain.patron.PatronType.Regular;
import static io.pillopl.newmodel.lending.domain.patron.PatronType.Researcher;


public class Fixtures {

    /**
     *
     * TODO: Fixture needs to be completed here.
     *
     */
    public static Patron aRegularPatron() {
        return new Patron(new PatronId(UUID.randomUUID()), Regular, new Holds(new HashSet<>()), 0);
    }

    /**
     *
     * TODO: Fixture needs to be completed here.
     *
     */
    public static Patron aResearcherPatron() {
        return new Patron(new PatronId(UUID.randomUUID()), Researcher, new Holds(new HashSet<>()), 0);
    }

    /**
     *
     * TODO: Fixture needs to be completed here.
     *
     */
    public static Patron aResearcherPatronWithTwoOverdueBooks() {
        return new Patron(new PatronId(UUID.randomUUID()), Researcher, new Holds(new HashSet<>()), 2);
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

