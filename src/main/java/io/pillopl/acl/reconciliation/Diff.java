package io.pillopl.acl.reconciliation;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

public class Diff<T> {

    Diff(Collection<T> oldOne, Collection<T> newOne) {
        this.presentInOldNotPresentInNew = CollectionUtils.subtract(oldOne, newOne);
        this.presentInNewNotPresentInOld = CollectionUtils.subtract(newOne, oldOne);
    }

    private final Collection<T> presentInOldNotPresentInNew;
    private final Collection<T> presentInNewNotPresentInOld;

    boolean exists() {
        return !presentInNewNotPresentInOld.isEmpty() || !presentInOldNotPresentInNew.isEmpty();
    }

    Collection<T> getPresentJustInOld() {
        return presentInOldNotPresentInNew;
    }

    Collection<T> getPresentJustInNew() {
        return presentInNewNotPresentInOld;
    }


}
