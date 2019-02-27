package io.pillopl.newmodel.lending.domain.patron;

import lombok.NonNull;
import lombok.Value;

import java.time.Period;

@Value
public class CollectDuration {

    public static CollectDuration forOneMonth() {
        return new CollectDuration(Period.ofMonths(1));
    }

    @NonNull Period period;

    CollectDuration(Period period) {
        if (period.getDays() > 60) {
            throw new IllegalArgumentException("Can checkout max for 60 days");
        }
        this.period = period;
    }

}
