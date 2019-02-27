package io.pillopl.newmodel.lending.application;

import io.pillopl.newmodel.lending.application.readmodel.CollectedBooksView;
import io.pillopl.newmodel.lending.application.readmodel.LendingQueryFacade;
import io.pillopl.newmodel.lending.application.readmodel.PlacedOnHoldBooksView;
import io.pillopl.newmodel.lending.domain.patron.PatronId;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LendingFacade {

    private final CollectService collectService;
    private final PlaceOnHoldService placeOnHoldService;
    private final LendingQueryFacade queryFacade;

    public Result execute(PlaceOnHoldCommand command) {
        return null; //TODO call application layer
    }

    public Result execute(CollectCommand command) {
        return null; //TODO call application layer
    }

    public PlacedOnHoldBooksView booksPlacedOnHoldBy(PatronId patronId) {
        return null; //TODO query read model
    }

    public CollectedBooksView booksCollectedBy(PatronId patronId) {
        return null; //TODO query read model
    }

}

