package io.pillopl.acl;

import io.pillopl.acl.reconciliation.Reconciliation;
import io.pillopl.acl.toggles.NewModelToggles;
import io.pillopl.bigballofmud.dtos.BookDto;
import io.pillopl.bigballofmud.dtos.BookRequest;
import io.pillopl.newmodel.lending.application.LendingFacade;

import java.util.*;

public class LendingACL {

    private final Reconciliation<BookDto> reconciliation;
    private final LendingFacade lendingFacade;

    public LendingACL(Reconciliation<BookDto> reconciliation, LendingFacade lendingFacade) {
        this.reconciliation = reconciliation;
        this.lendingFacade = lendingFacade;
    }

    public List<BookDto> booksPlacedOnHoldBy(UUID patronId, List<BookDto> oldModelResult) {
        if (NewModelToggles.RECONCILE_AND_USE_NEW_MODEL.isActive()) {
            List<BookDto> newModelResult = new ArrayList<>();//TODO callNewModel
            newModelResult.add(new BookDto());
            reconciliation.compare(toSet(oldModelResult), toSet(newModelResult));
            return newModelResult;
        }
        if (NewModelToggles.RECONCILE_NEW_MODEL.isActive()) {
            List<BookDto> newModelResult = new ArrayList<>();//TODO callNewModel
            newModelResult.add(new BookDto());
            reconciliation.compare(toSet(oldModelResult), toSet(newModelResult));
            return oldModelResult;
        }
        return oldModelResult;
    }

    /**
     * Task #3:
     * Implement this. Make sure tests in LendingACL pass.
     */
    public List<BookDto> booksCurrentlyCollectedBy(UUID patronId, List<BookDto> oldModelResult) {
        return oldModelResult;
    }


    private Set<BookDto> toSet(List<BookDto> books) {
        return new HashSet<>(books);
    }

    public void placeOnHold(BookRequest bookRequest) {

    }
}
