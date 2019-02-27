package io.pillopl.newmodel.lending.domain.patron;

import lombok.NonNull;
import lombok.Value;

import java.time.Period;
import java.util.Optional;

public interface HoldDuration {

    static HoldDuration forTenDays() {
        return forDays(10);
    }

    static HoldDuration forDays(int days) {
        return new CloseEndedHoldDuration(Period.ofDays(days));
    }

    static HoldDuration openEnded() {
        return new OpenEndedHoldDuration();
    }

    Optional<Integer> getDays();

    default boolean isCloseEnded() {
        return getDays().isPresent();
    }
}

@Value
class CloseEndedHoldDuration implements HoldDuration {

    @NonNull Period period;

    CloseEndedHoldDuration(Period period) {
        this.period = period;
    }

    @Override
    public Optional<Integer> getDays() {
        return Optional.of(period.getDays());
    }
}

@Value
class OpenEndedHoldDuration implements HoldDuration {

    @Override
    public Optional<Integer> getDays() {
        return Optional.empty();
    }
}