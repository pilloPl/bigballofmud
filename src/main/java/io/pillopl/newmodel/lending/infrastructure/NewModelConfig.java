package io.pillopl.newmodel.lending.infrastructure;

import io.pillopl.newmodel.DomainEvents;
import io.pillopl.newmodel.lending.application.CollectService;
import io.pillopl.newmodel.lending.application.LendingFacade;
import io.pillopl.newmodel.lending.application.PlaceOnHoldService;
import io.pillopl.newmodel.lending.application.readmodel.LendingQueryFacade;
import io.pillopl.newmodel.lending.domain.patron.PatronRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NewModelConfig {

    @Bean
    LendingFacade lendingFacade(CollectService collectService, PlaceOnHoldService placeOnHoldService, LendingQueryFacade lendingQueryFacade) {
       return new LendingFacade(collectService, placeOnHoldService, lendingQueryFacade);
    }

    @Bean
    LendingQueryFacade lendingQueryFacade() {
        return new LendingQueryFacade();
    }

    @Bean
    PatronRepository patronRepository() {
        return new InMemoryPatronRepository();
    }

    @Bean
    DomainEvents domainEvents(ApplicationEventPublisher applicationEventPublisher) {
        return new InMemoryDomainEvents(applicationEventPublisher);
    }

    @Bean
    InMemoryBookRepository bookNewModelRepository() {
        return new InMemoryBookRepository();
    }

    @Bean
    PlaceOnHoldService placeOnHoldService(PatronRepository patronRepository, InMemoryBookRepository bookRepository, DomainEvents domainEvents) {
        return new PlaceOnHoldService(patronRepository, bookRepository, domainEvents);
    }

    @Bean
    CollectService collectService(PatronRepository patronRepository, DomainEvents domainEvents) {
        return new CollectService(patronRepository, domainEvents);
    }
}
