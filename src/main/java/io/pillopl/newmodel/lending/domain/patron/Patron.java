package io.pillopl.newmodel.lending.domain.patron;

import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;
import io.pillopl.newmodel.lending.domain.patron.events.BookCollected;
import io.pillopl.newmodel.lending.domain.patron.events.BookPlacedOnHold;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;


/**
 * Task #2: Implement this aggregate
 * Make PlacingOnHoldBookScenarios and CollectingPlacedOnHoldBookScenarios pass!
 * Remember that Fixtures, for instance aRegularPatron() should be changed so that it returns a regular patron in the meaning of your new model.
 * There is one scenario to implement in CollectingPlacedOnHoldBookScenarios too. Implement it, make sure it passes.
 * Think about why we pass AvailableBook instead of Book to placeOnHold() method. Does it have any benefit?
 */
@AllArgsConstructor
public class Patron {

    @NonNull @Getter
    final PatronId patronId;

    public Optional<BookPlacedOnHold> placeOnHold(AvailableBook book, HoldDuration holdDuration) {
       return Optional.empty();
    }

    public Optional<BookCollected> collect(BookId bookId, CollectDuration collectDuration) {
        return Optional.empty();
    }



}
