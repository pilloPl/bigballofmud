package io.pillopl.newmodel.lending.application;

import io.pillopl.newmodel.DomainEvents;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;
import io.pillopl.newmodel.lending.domain.patron.Patron;
import io.pillopl.newmodel.lending.domain.patron.PatronRepository;
import io.pillopl.newmodel.lending.domain.patron.events.BookPlacedOnHold;
import lombok.AllArgsConstructor;

import java.util.Optional;

import static io.pillopl.newmodel.lending.application.Result.Allowance;
import static io.pillopl.newmodel.lending.application.Result.Rejection;

@AllArgsConstructor
public class PlaceOnHoldService {

    private final PatronRepository patronRepository;
    private final FindAvailableBook findAvailableBook;
    private final DomainEvents domainEvents;

    Result placeOnHold(PlaceOnHoldCommand command) {
        Patron patron = findPatron(command);
        AvailableBook book = findAvailableBook(command);
        Optional<BookPlacedOnHold> event = patron.placeOnHold(book, command.getHoldDuration());
        event.ifPresent(domainEvents::publish);
        patronRepository.save(patron);
        return event.map(evt -> Allowance).orElse(Rejection);

    }

    private AvailableBook findAvailableBook(PlaceOnHoldCommand command) {
        return findAvailableBook.find(command.getBookId()).orElseThrow(IllegalArgumentException::new);
    }

    private Patron findPatron(PlaceOnHoldCommand command) {
        return patronRepository.findById(command.getPatronId()).orElseThrow(IllegalArgumentException::new);
    }

}

