package io.pillopl.newmodel;

public interface DomainEvents {

    void publish(DomainEvent domainEvent);
}
