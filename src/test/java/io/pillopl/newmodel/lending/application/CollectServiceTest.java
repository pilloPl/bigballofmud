package io.pillopl.newmodel.lending.application;

import io.pillopl.newmodel.DomainEvents;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;
import io.pillopl.newmodel.lending.domain.patron.*;
import io.pillopl.newmodel.lending.domain.patron.events.BookCollected;
import io.pillopl.newmodel.lending.infrastructure.InMemoryPatronRepository;
import org.junit.Test;

import static io.pillopl.newmodel.lending.domain.patron.CollectDuration.forOneMonth;
import static io.pillopl.newmodel.lending.domain.patron.Fixtures.circulatingBook;
import static io.pillopl.newmodel.lending.domain.patron.HoldDuration.forTenDays;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

public class CollectServiceTest {

    PatronRepository patronRepository = new InMemoryPatronRepository();
    DomainEvents events = mock(DomainEvents.class);

    CollectService collectService = new CollectService(patronRepository, events);

    @Test
    public void shouldPublishAnEventWhenOperationWasSuccessful() {
        //given
        Patron patron = persistedRegularPatron();
        //and
        AvailableBook book = circulatingBook();
        //and

        patron.placeOnHold(book, forTenDays());

        //when
        collectService.placeOnHold(new CollectCommand(forOneMonth(), book.getBookId(), patron.getPatronId()));

        //then
        verify(events).publish(isA(BookCollected.class));

    }

    @Test
    public void shouldNotPublishAnEventWhenOperationWasNotSuccessful() {
        //given
        Patron patron = persistedRegularPatron();
        //and
        AvailableBook book = circulatingBook();

        //when
        collectService.placeOnHold(new CollectCommand(forOneMonth(), book.getBookId(), patron.getPatronId()));

        //then
        verifyZeroInteractions(events);
    }

    Patron persistedRegularPatron() {
        Patron patron = Fixtures.aRegularPatron();
        patronRepository.save(patron);
        return patron;
    }

}