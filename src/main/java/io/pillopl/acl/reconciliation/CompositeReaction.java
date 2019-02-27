package io.pillopl.acl.reconciliation;

import io.pillopl.acl.reconciliation.Reconciliation.Reaction;

import java.util.Arrays;
import java.util.List;

public class CompositeReaction implements Reaction {

    private final List<Reaction> reactions;

    CompositeReaction(Reaction... reactions) {
        this.reactions = Arrays.asList(reactions);
    }

    @Override
    public void reactTo(Diff diff) {
        reactions.forEach(reaction -> reaction.reactTo(diff));
    }

    private void disableToggle() {
        //..
    }

    private void sendMail() {
        //..
    }
}
