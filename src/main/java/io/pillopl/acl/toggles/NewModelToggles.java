package io.pillopl.acl.toggles;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum NewModelToggles implements Feature {

    @Label("Just reconcile new model")
    RECONCILE_NEW_MODEL,

    @Label("Reconcile and use new model")
    RECONCILE_AND_USE_NEW_MODEL;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }

}