package io.pillopl.newmodel.lending.application;

import io.pillopl.newmodel.catalogue.BookId;
import io.pillopl.newmodel.lending.domain.patron.CollectDuration;
import io.pillopl.newmodel.lending.domain.patron.PatronId;
import lombok.NonNull;
import lombok.Value;

@Value
public class CollectCommand {

    @NonNull
    CollectDuration collectDuration;
    @NonNull
    BookId bookId;
    @NonNull
    PatronId patronId;
}
