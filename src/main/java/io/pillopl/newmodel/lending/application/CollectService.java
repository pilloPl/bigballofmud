package io.pillopl.newmodel.lending.application;

import io.pillopl.newmodel.DomainEvents;
import io.pillopl.newmodel.lending.domain.patron.Patron;
import io.pillopl.newmodel.lending.domain.patron.PatronRepository;
import io.pillopl.newmodel.lending.domain.patron.events.BookCollected;
import lombok.AllArgsConstructor;

import java.util.Optional;

import static io.pillopl.newmodel.lending.application.Result.Allowance;
import static io.pillopl.newmodel.lending.application.Result.Rejection;

@AllArgsConstructor
public class CollectService {

    private final PatronRepository patronRepository;
    private final DomainEvents domainEvents;

    Result placeOnHold(CollectCommand command) {
        Patron patron = findPatron(command);
        Optional<BookCollected> event = patron.collect(command.getBookId(), command.getCollectDuration());
        event.ifPresent(domainEvents::publish);
        patronRepository.save(patron);
        return event.map(evt -> Allowance).orElse(Rejection);
    }

    private Patron findPatron(CollectCommand command) {
        return patronRepository.findById(command.getPatronId()).orElseThrow(IllegalArgumentException::new);
    }

}

