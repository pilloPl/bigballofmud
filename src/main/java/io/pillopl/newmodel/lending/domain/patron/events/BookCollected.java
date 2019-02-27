package io.pillopl.newmodel.lending.domain.patron.events;

import io.pillopl.newmodel.DomainEvent;
import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.patron.PatronId;
import lombok.Value;

@Value
public class BookCollected implements DomainEvent {
    BookId bookId;
    PatronId patronId;
    int forDays;

}

