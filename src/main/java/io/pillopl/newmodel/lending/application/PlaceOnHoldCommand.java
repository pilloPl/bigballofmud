package io.pillopl.newmodel.lending.application;

import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.patron.HoldDuration;
import io.pillopl.newmodel.lending.domain.patron.PatronId;
import lombok.NonNull;
import lombok.Value;

@Value
public class PlaceOnHoldCommand {

    @NonNull
    BookId bookId;
    @NonNull
    PatronId patronId;
    @NonNull
    HoldDuration holdDuration;
}
