package io.pillopl.newmodel.lending.application.readmodel;

import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.patron.PatronId;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PlacedOnHoldBooksView {

    PlacedOnHoldBooksView(PatronId patronId) {
        this.patronId = patronId;
    }

    private PatronId patronId;

    private List<BookId> books = new ArrayList<>();

    void addBook(BookId book) {
        books.add(book);
    }

    void removeBook(BookId book) {
        books.remove(book);
    }

}


