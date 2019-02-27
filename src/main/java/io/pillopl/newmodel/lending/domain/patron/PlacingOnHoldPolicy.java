package io.pillopl.newmodel.lending.domain.patron;

import io.pillopl.newmodel.lending.domain.book.AvailableBook;

import java.util.Arrays;
import java.util.List;

interface PlacingOnHoldPolicy {
    boolean canPlaceOnHold(AvailableBook availableBook, Patron patron, HoldDuration holdDuration);

    static PlacingOnHoldPolicy allPolicies() {
        return new CompositePlaceOnHoldPolicy(new RegularPatronCannotHoldRestrictedBooks(), new MaximumHoldsPolicy(), new CannotPlaceOnHoldWhenTwoOverdueBooks(), new OnlyResearcherCanPlaceOpenEndedHold());
    }
}

class RegularPatronCannotHoldRestrictedBooks implements PlacingOnHoldPolicy {

    public boolean canPlaceOnHold(AvailableBook availableBook, Patron patron, HoldDuration holdDuration) {
        return !patron.isRegular() || !availableBook.isRestricted();
    }
}

class MaximumHoldsPolicy implements PlacingOnHoldPolicy {

    public boolean canPlaceOnHold(AvailableBook availableBook, Patron patron, HoldDuration holdDuration) {
        return patron.numberOfHolds() < 5;
    }

}

class OnlyResearcherCanPlaceOpenEndedHold implements PlacingOnHoldPolicy {

    public boolean canPlaceOnHold(AvailableBook availableBook, Patron patron, HoldDuration holdDuration) {
        if (holdDuration.isCloseEnded()) {
            return true;
        } else {
            return !patron.isRegular();
        }
    }

}

class CannotPlaceOnHoldWhenTwoOverdueBooks implements PlacingOnHoldPolicy {

    public boolean canPlaceOnHold(AvailableBook availableBook, Patron patron, HoldDuration holdDuration) {
        return patron.numberOfOverdueBooks() < 2;
    }

}

class CompositePlaceOnHoldPolicy implements PlacingOnHoldPolicy {

    CompositePlaceOnHoldPolicy(PlacingOnHoldPolicy... policies) {
        this.policies = Arrays.asList(policies);
    }

    private final List<PlacingOnHoldPolicy> policies;

    @Override
    public boolean canPlaceOnHold(AvailableBook availableBook, Patron patron, HoldDuration holdDuration) {
        return policies.stream().allMatch(policy -> policy.canPlaceOnHold(availableBook, patron, holdDuration));
    }
}
