package io.pillopl.newmodel.lending.infrastructure;

import io.pillopl.newmodel.DomainEvent;
import io.pillopl.newmodel.DomainEvents;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@AllArgsConstructor
class InMemoryDomainEvents implements DomainEvents {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent domainEvent) {
        applicationEventPublisher.publishEvent(domainEvent);
    }
}

