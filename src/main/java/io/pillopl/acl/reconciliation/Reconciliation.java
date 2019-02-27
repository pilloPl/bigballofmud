package io.pillopl.acl.reconciliation;

import io.pillopl.acl.toggles.NewModelToggles;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public class Reconciliation<T> {

    public interface Reaction {
        void reactTo(Diff diff);

        static Reaction logAndThanDisableToggle() {
            return new CompositeReaction(justLog(), diff -> NewModelToggles.RECONCILE_NEW_MODEL.isActive());
        }

        static Reaction justLog() {
            return System.out::println;
        }
    }

    private final Reaction reaction;

    public Reconciliation(Reaction reaction) {
        this.reaction = reaction;
    }

    public Diff<T> compare(Set<T> oldOne, Set<T> newOne) {
        requireNonNull(oldOne);
        requireNonNull(newOne);
        Diff<T> difference = new Diff<>(oldOne, newOne);
        if (difference.exists()) {
            reaction.reactTo(difference);
        }
        return difference;
    }
}
