package io.pillopl.bigballofmud;

import io.pillopl.acl.AclConfiguration;
import io.pillopl.acl.toggles.NewModelToggles;
import io.pillopl.bigballofmud.controllers.BookController;
import io.pillopl.bigballofmud.dtos.BookDto;
import io.pillopl.bigballofmud.dtos.BookRequest;
import io.pillopl.bigballofmud.entities.BookEntity;
import io.pillopl.bigballofmud.entities.BookHolderEntity;
import io.pillopl.bigballofmud.exceptions.InvalidBookCollectionStateException;
import io.pillopl.bigballofmud.exceptions.InvalidBookLendingStateException;
import io.pillopl.bigballofmud.rabbitmq.QueueListener;
import io.pillopl.bigballofmud.repositories.BookRepository;
import io.pillopl.bigballofmud.services.HolderRentalFeeService;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.togglz.junit.TogglzRule;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.pillopl.bigballofmud.entities.BookEntity.BookLendingState.Collected;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BigBallOfMud.class, AclConfiguration.class})
public class ModelsEqualityFromObservableBehaviorsCheck {

    @Autowired
    BookController bookController;

    @Autowired
    Fixtures fixtures;

    @Autowired
    HolderRentalFeeService holderRentalFeeService;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    QueueListener queueListener;


    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(NewModelToggles.class);

    @After
    public void cleanup() {
        togglzRule.disableAll();
    }


    @Test
    public void regularPatronCannotHoldRestrictedBooks() {
        //given
        BookEntity restrictedBook = fixtures.aRestrictedBookAvailableForLending();

        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();

        //expect
        assertThatExceptionOfType(InvalidBookLendingStateException.class).isThrownBy(() -> patronWantsToHoldBook(aRegularPatron, restrictedBook));
    }

    @Test
    public void researcherPatronCanHoldRestrictedBooks() {
        //given
        BookEntity restrictedBook = fixtures.aRestrictedBookAvailableForLending();

        //and
        BookHolderEntity aResearcherPatron = fixtures.aResearcherPatron();

        //when
        patronWantsToHoldBook(aResearcherPatron, restrictedBook);

        //then
        assertThat(oldModelPlacedOnHoldsBooksBy(aResearcherPatron)).containsExactlyInAnyOrderElementsOf(newModelPlacedOnHoldsBooksBy(aResearcherPatron));

    }

    @Test
    public void patronCanHoldABook() {
        //given
        BookEntity circulatedBook = fixtures.aCirculatingBookAvailableForLending();
        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();

        //when
        patronWantsToHoldBook(aRegularPatron, circulatedBook);

        //then
        assertThat(oldModelPlacedOnHoldsBooksBy(aRegularPatron)).containsExactlyInAnyOrderElementsOf(newModelPlacedOnHoldsBooksBy(aRegularPatron));
    }

    @Test
    public void patronCanHoldUpTo5Books() {
        //given
        BookEntity circulatedBook1 = fixtures.aCirculatingBookAvailableForLending();
        BookEntity circulatedBook2 = fixtures.aCirculatingBookAvailableForLending();
        BookEntity circulatedBook3 = fixtures.aCirculatingBookAvailableForLending();
        BookEntity circulatedBook4 = fixtures.aCirculatingBookAvailableForLending();
        BookEntity circulatedBook5 = fixtures.aCirculatingBookAvailableForLending();
        BookEntity circulatedBook6 = fixtures.aCirculatingBookAvailableForLending();

        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();

        //and
        patronWantsToHoldBook(aRegularPatron, circulatedBook1, circulatedBook2, circulatedBook3, circulatedBook4, circulatedBook5);

        //expect
        assertThatExceptionOfType(InvalidBookLendingStateException.class).isThrownBy(() -> patronWantsToHoldBook(aRegularPatron, circulatedBook6));
    }

    @Test
    public void researcherCanPlaceOpenEndedHolds() {
        //given
        BookEntity aCirculatingBook = fixtures.aCirculatingBookAvailableForLending();
        //and
        BookHolderEntity aResearcherPatron = fixtures.aResearcherPatron();

        //when
        patronWantsToHoldBookForOpenEndedHold(aResearcherPatron, aCirculatingBook);

        //then
        assertThat(oldModelPlacedOnHoldsBooksBy(aResearcherPatron)).containsExactlyInAnyOrderElementsOf(newModelPlacedOnHoldsBooksBy(aResearcherPatron));
    }

    @Test
    public void regularPatronCannotPlaceOpenEndedHolds() {
        //given
        BookEntity aCirculatingBook = fixtures.aCirculatingBookAvailableForLending();
        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();

        //expect
        assertThatExceptionOfType(InvalidBookLendingStateException.class).isThrownBy(() -> patronWantsToHoldBookForOpenEndedHold(aRegularPatron, aCirculatingBook));

    }


    @Test
    public void patronCannotHoldBooksWhenThereAreTwoOverdueCollections() {
        //given
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();
        //and
        patronHasTwoCollectedBooksTillYesterday(aRegularPatron);

        //and
        BookEntity book = fixtures.aCirculatingBookAvailableForLending();

        //expect
        assertThatExceptionOfType(InvalidBookLendingStateException.class).isThrownBy(() -> patronWantsToHoldBook(aRegularPatron, book));

    }

    @Test
    public void patronCanHoldBooksWhenThereIsOneOverdueCollection() {
        //given
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();
        //and
        thereIsABookCollectedTillYesterdayBy(aRegularPatron);
        //and
        BookEntity book = fixtures.aCirculatingBookAvailableForLending();

        //when
        patronWantsToHoldBook(aRegularPatron, book);

        //then
        assertThat(oldModelPlacedOnHoldsBooksBy(aRegularPatron)).containsExactlyInAnyOrderElementsOf(newModelPlacedOnHoldsBooksBy(aRegularPatron));

    }


    @Test
    public void cannotPlaceOnHoldBookWhichIsHeldBySomeoneElse() {
        //given
        BookEntity circulatedBook = fixtures.aCirculatingBookAvailableForLending();
        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();
        //and
        BookHolderEntity anotherRegularPatron = fixtures.aRegularPatron();
        //and
        patronWantsToHoldBook(aRegularPatron, circulatedBook);

        //expect
        assertThatExceptionOfType(InvalidBookLendingStateException.class).isThrownBy(() -> patronWantsToHoldBook(anotherRegularPatron, circulatedBook));
    }

    @Test
    public void canCollectABook() {
        //given
        BookEntity circulatedBook = fixtures.aCirculatingBookAvailableForLending();
        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();
        //and
        patronWantsToHoldBook(aRegularPatron, circulatedBook);

        //when
        patronWantsToCollectTheBook(aRegularPatron, circulatedBook, 5);

        //then
        assertThat(oldModelCollectedBooksBy(aRegularPatron)).containsExactlyInAnyOrderElementsOf(newModelCollectedBooksBy(aRegularPatron));
        assertThat(oldModelPlacedOnHoldsBooksBy(aRegularPatron)).containsExactlyInAnyOrderElementsOf(oldModelPlacedOnHoldsBooksBy(aRegularPatron));
    }


    @Test
    public void cannotCollectBookNotPlacedOnHold() {
        //given
        BookEntity circulatedBook = fixtures.aCirculatingBookAvailableForLending();
        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();

        //expect
        assertThatExceptionOfType(InvalidBookCollectionStateException.class).isThrownBy(() -> patronWantsToCollectTheBook(aRegularPatron, circulatedBook, 5));

    }

    @Test
    public void bookCanBeCollectedForUpTo60days() {
        //given
        BookEntity circulatedBook = fixtures.aCirculatingBookAvailableForLending();
        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();
        //and
        patronWantsToHoldBook(aRegularPatron, circulatedBook);

        //expect
        assertThatExceptionOfType(InvalidBookCollectionStateException.class).isThrownBy(() -> patronWantsToCollectTheBook(aRegularPatron, circulatedBook, 555));
    }

    @Test
    public void canReturnABook() {
        //given
        BookEntity circulatedBook = fixtures.aCirculatingBookAvailableForLending();
        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();
        //and
        patronWantsToHoldBook(aRegularPatron, circulatedBook);
        //and
        patronWantsToCollectTheBook(aRegularPatron, circulatedBook, 5);

        //when
        patronWantsToReturnTheBook(aRegularPatron, circulatedBook);

        //then
        assertThat(oldModelCollectedBooksBy(aRegularPatron)).isEmpty();
        assertThat(oldModelPlacedOnHoldsBooksBy(aRegularPatron)).isEmpty();
    }


    @Test
    public void canCollectABookListeningToRabbitMq() {
        //given
        BookEntity circulatedBook = fixtures.aCirculatingBookAvailableForLending();
        //and
        BookHolderEntity aRegularPatron = fixtures.aRegularPatron();
        //and
        patronWantsToHoldBook(aRegularPatron, circulatedBook);

        //when
        queueListener.collect(new BookRequest(circulatedBook.getId(), aRegularPatron.getId(), 10, false));

        //then
        assertThat(oldModelCollectedBooksBy(aRegularPatron)).containsExactlyInAnyOrderElementsOf(newModelCollectedBooksBy(aRegularPatron));
        assertThat(oldModelPlacedOnHoldsBooksBy(aRegularPatron)).containsExactlyInAnyOrderElementsOf(newModelPlacedOnHoldsBooksBy(aRegularPatron));
    }

    void patronWantsToHoldBook(BookHolderEntity patron, BookEntity... books) {
        Arrays.asList(books).forEach(book -> bookController.addHold(new BookRequest(book.getId(), patron.getId(), 10, false)));
    }

    void patronWantsToHoldBookForOpenEndedHold(BookHolderEntity patron, BookEntity... books) {
        Arrays.asList(books).forEach(book -> bookController.addHold(new BookRequest(book.getId(), patron.getId(), null, true)));
    }

    void patronWantsToCollectTheBook(BookHolderEntity patron, BookEntity book, int days) {
        bookController.collect(new BookRequest(book.getId(), patron.getId(), days, true));
    }

    void patronWantsToReturnTheBook(BookHolderEntity patron, BookEntity book) {
        bookController.removeCollectedBook(new BookRequest(book.getId(), patron.getId(), null, null));
    }


    List<UUID> oldModelPlacedOnHoldsBooksBy(BookHolderEntity aRegularPatron) {
        return bookController.getPlacedOnHoldBooks(aRegularPatron.getId()).getBody().stream().map(BookDto::getBookId).collect(Collectors.toList());
    }

    List<UUID> newModelPlacedOnHoldsBooksBy(BookHolderEntity aRegularPatron) {
        togglzRule.enable(NewModelToggles.RECONCILE_AND_USE_NEW_MODEL);
        List<UUID> collect = oldModelPlacedOnHoldsBooksBy(aRegularPatron);
        togglzRule.disable(NewModelToggles.RECONCILE_AND_USE_NEW_MODEL);
        return collect;
    }

    List<UUID> oldModelCollectedBooksBy(BookHolderEntity aRegularPatron) {
        return bookController.getCollectedBooks(aRegularPatron.getId()).getBody().stream().map(BookDto::getBookId).collect(Collectors.toList());
    }

    List<UUID> newModelCollectedBooksBy(BookHolderEntity aRegularPatron) {
        togglzRule.enable(NewModelToggles.RECONCILE_AND_USE_NEW_MODEL);
        List<UUID> collect = oldModelCollectedBooksBy(aRegularPatron);
        togglzRule.disable(NewModelToggles.RECONCILE_AND_USE_NEW_MODEL);
        return collect;
    }



    void patronHasTwoCollectedBooksTillYesterday(BookHolderEntity aRegularPatron) {
        thereIsABookCollectedTillYesterdayBy(aRegularPatron);
        thereIsABookCollectedTillYesterdayBy(aRegularPatron);
    }

    void thereIsABookCollectedTillYesterdayBy(BookHolderEntity aRegularPatron) {
        BookEntity bookEntity = fixtures.aCirculatingBookAvailableForLending();
        patronWantsToHoldBook(aRegularPatron, bookEntity);
        patronWantsToCollectTheBook(aRegularPatron, bookEntity, 1);
        bookEntity.setCollectedFrom(Instant.now().minus(Duration.ofDays(2)));
        bookEntity.setCollectedTill(Instant.now().minus(Duration.ofDays(1)));
        bookEntity.setLendingState(Collected);
        bookRepository.save(bookEntity);
    }

}


