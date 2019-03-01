package io.pillopl.newmodel.lending.domain.patron;

import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;
import io.pillopl.newmodel.lending.domain.patron.events.BookCollected;
import io.pillopl.newmodel.lending.domain.patron.events.BookPlacedOnHold;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.*;

import static io.pillopl.newmodel.lending.domain.patron.PatronType.Researcher;


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
    @Getter
    final PatronId patronId;

    final PatronType type;

    Holds patronHolds;

    int overdueCollections;

    public Optional<BookPlacedOnHold> placeOnHold(AvailableBook book, HoldDuration holdDuration, PlacingOnHoldPolicy placingOnHoldPolicy) {
        if (placingOnHoldPolicy.canPlaceOnHold(book, this, holdDuration)) {
            patronHolds = patronHolds.with(book.getBookId());
            return Optional.of(new BookPlacedOnHold(book.getBookId(), patronId, holdDuration.getDays().orElse(null)));

        }
        return Optional.empty();
    }

    public Optional<BookPlacedOnHold> placeOnHold(AvailableBook book, HoldDuration holdDuration) {
        return placeOnHold(book, holdDuration, new CompositePolicy(new MaxHoldsPolicy(5), new MaxOverdueBooks(2), new OnlyResearcherCanPlaceOnHoldRestricted(), new OnlyResearcherCanPlaceOpenHolds()));
    }


    public Optional<BookCollected> collect(BookId bookId, CollectDuration collectDuration) {
        if(patronHolds.a(bookId)) {
            patronHolds = patronHolds.without(bookId);
            return Optional.of(new BookCollected(bookId, patronId, collectDuration.getPeriod().getDays()));
        }
        return Optional.empty();
    }


    int holds() {
        return patronHolds.count();
    }

    int overdueCollections() {
        return overdueCollections;
    }

    boolean isResearcher() {
        return type.equals(Researcher);
    }
}


@Value
class Holds {

    Set<BookId> books;

    Holds with(BookId bookId) {
        Set<BookId> newBooks = new HashSet<>(books);
        newBooks.add(bookId);
        return new Holds(newBooks);
    }

    Holds without(BookId bookId) {
        Set<BookId> newBooks = new HashSet<>(books);
        newBooks.remove(bookId);
        return new Holds(newBooks);
    }

    boolean a(BookId bookId) {
        return books.contains(bookId);
    }

    int count() {
        return books.size();
    }
}


interface PlacingOnHoldPolicy {

    boolean canPlaceOnHold(AvailableBook book, Patron patron, HoldDuration duration);
}


@Value
class MaxHoldsPolicy implements PlacingOnHoldPolicy {

    int maxHolds;

    @Override
    public boolean canPlaceOnHold(AvailableBook book, Patron patron, HoldDuration duration) {
        return patron.holds() < 5;
    }
}

@Value
class MaxOverdueBooks implements PlacingOnHoldPolicy {

    int maxOverdueBooks;

    @Override
    public boolean canPlaceOnHold(AvailableBook book, Patron patron, HoldDuration duration) {
        return patron.overdueCollections() < 2;
    }
}

@Value
class OnlyResearcherCanPlaceOnHoldRestricted implements PlacingOnHoldPolicy {


    @Override
    public boolean canPlaceOnHold(AvailableBook book, Patron patron, HoldDuration duration) {
        if (book.isRestricted()) {
            return patron.isResearcher();
        }
        return true;
    }
}

@Value
class OnlyResearcherCanPlaceOpenHolds implements PlacingOnHoldPolicy {

    @Override
    public boolean canPlaceOnHold(AvailableBook book, Patron patron, HoldDuration duration) {
        if (duration.isOpenEnded()) {
            return patron.isResearcher();
        }
        return true;
    }
}


@Value
class CompositePolicy implements PlacingOnHoldPolicy {

    List<PlacingOnHoldPolicy> policies;

    CompositePolicy(PlacingOnHoldPolicy... policies) {
        this.policies = Arrays.asList(policies);
    }

    @Override
    public boolean canPlaceOnHold(AvailableBook book, Patron patron, HoldDuration duration) {
        return policies.stream().allMatch(policy -> policy.canPlaceOnHold(book, patron, duration));
    }
}



