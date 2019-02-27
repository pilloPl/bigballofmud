package io.pillopl.newmodel.lending.application;

import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;

import java.util.Optional;

public interface FindAvailableBook {

    Optional<AvailableBook> find(BookId bookId);
}
