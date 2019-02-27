package io.pillopl.newmodel.lending.domain.patron;

import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;
import io.pillopl.newmodel.lending.domain.patron.events.BookCollected;
import io.pillopl.newmodel.lending.domain.patron.events.BookPlacedOnHold;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static io.pillopl.newmodel.lending.domain.patron.PatronType.Regular;
import static io.pillopl.newmodel.lending.domain.patron.PlacingOnHoldPolicy.allPolicies;


/**
 * Task #2: Implement this aggregate
 * Make PlacingOnHoldBookScenarios and CollectingPlacedOnHoldBookScenarios pass!
 * Remember that Fixtures, for instance aRegularPatron() should be changed so that it returns a regular patron in the meaning of your new model.
 * There is one scenario to implement in CollectingPlacedOnHoldBookScenarios too. Implement it, make sure it passes.
 * Think about why we pass AvailableBook instead of Book to placeOnHold() method. Does it have any benefit?
 */
@AllArgsConstructor
public class Patron {

    @NonNull
    final PatronType patronType;

    @NonNull @Getter
    final PatronId patronId;

    @NonNull
    Holds holds;

    @NonNull
    OverdueCollectedBooks overdueCollectedBooks;

    public Optional<BookPlacedOnHold> placeOnHold(AvailableBook book, HoldDuration holdDuration) {
        return placeOnHold(book, holdDuration, allPolicies());
    }

    public Optional<BookPlacedOnHold> placeOnHold(AvailableBook book, HoldDuration holdDuration, PlacingOnHoldPolicy placeOnHoldPolicy) {
        if (placeOnHoldPolicy.canPlaceOnHold(book, this, holdDuration)) {
            holds = holds.with(book.getBookId());
            return Optional.of(new BookPlacedOnHold(book.getBookId(), patronId, holdDuration.getDays().orElse(null)));
        }
        return Optional.empty();
    }

    public Optional<BookCollected> collect(BookId bookId, CollectDuration collectDuration) {
        if (holds.containsHoldFor(bookId)) {
            holds = holds.without(bookId);
            return Optional.of(new BookCollected(bookId, patronId, collectDuration.getPeriod().getDays()));
        }
        return Optional.empty();
    }

    boolean isRegular() {
        return patronType.equals(Regular);
    }

    int numberOfHolds() {
        return holds.getHolds().size();
    }

    int numberOfOverdueBooks() {
        return overdueCollectedBooks.count();
    }



}

@Value
class Holds {

    @NonNull Set<BookId> holds;

    Holds(Set<BookId> holds) {
        this.holds = holds;
    }

    Holds with(BookId bookId) {
        Set<BookId> newHolds = new HashSet<>(holds);
        newHolds.add(bookId);
        return new Holds(newHolds);
    }

    Holds without(BookId bookId) {
        Set<BookId> newHolds = new HashSet<>(holds);
        newHolds.remove(bookId);
        return new Holds(newHolds);
    }

    boolean containsHoldFor(BookId bookId) {
        return holds.contains(bookId);
    }
}

@Value
class OverdueCollectedBooks {

    @NonNull Set<BookId> overdueBooks;

    OverdueCollectedBooks(Set<BookId> overdueBooks) {
        this.overdueBooks = overdueBooks;
    }

    int count() {
        return overdueBooks.size();
    }
}
