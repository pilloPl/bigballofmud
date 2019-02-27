package io.pillopl.newmodel.lending.application;

import io.pillopl.newmodel.DomainEvents;
import io.pillopl.newmodel.lending.domain.book.AvailableBook;
import io.pillopl.newmodel.lending.domain.patron.Fixtures;
import io.pillopl.newmodel.lending.domain.patron.Patron;
import io.pillopl.newmodel.lending.domain.patron.PatronRepository;
import io.pillopl.newmodel.lending.domain.patron.events.BookPlacedOnHold;
import io.pillopl.newmodel.lending.infrastructure.InMemoryPatronRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static io.pillopl.newmodel.lending.domain.patron.HoldDuration.forTenDays;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

public class PlacedOnHoldServiceTest {

    PatronRepository patronRepository = new InMemoryPatronRepository();
    FindAvailableBook findAvailableBook = mock(FindAvailableBook.class);
    DomainEvents events = mock(DomainEvents.class);

    PlaceOnHoldService placeOnHoldService = new PlaceOnHoldService(patronRepository, findAvailableBook, events);

    @Test
    public void shouldPublishAnEventWhenOperationWasSuccessful() {
        //given
        Patron patron = persistedRegularPatron();
        //and
        AvailableBook book = availableBook();

        //when
        placeOnHoldService.placeOnHold(new PlaceOnHoldCommand(book.getBookId(), patron.getPatronId(), forTenDays()));

        //then
        verify(events).publish(isA(BookPlacedOnHold.class));
    }

    @Test
    public void shouldNotPublishAnEventWhenOperationWasNotSuccessful() {
        //given
        Patron patron = persistedRegularPatron();
        //and
        AvailableBook book = availableRestrictedBook();

        //when
        placeOnHoldService.placeOnHold(new PlaceOnHoldCommand(book.getBookId(), patron.getPatronId(), forTenDays()));

        //then
        verifyZeroInteractions(events);
    }

    Patron persistedRegularPatron() {
        Patron patron = Fixtures.aRegularPatron();
        patronRepository.save(patron);
        return patron;
    }

    AvailableBook availableBook() {
        AvailableBook book = Fixtures.circulatingBook();
        Mockito.when(findAvailableBook.find(Mockito.any())).thenReturn(Optional.of(book));
        return book;
    }

    AvailableBook availableRestrictedBook() {
        AvailableBook book = Fixtures.restrictedBook();
        Mockito.when(findAvailableBook.find(Mockito.any())).thenReturn(Optional.of(book));
        return book;
    }

}