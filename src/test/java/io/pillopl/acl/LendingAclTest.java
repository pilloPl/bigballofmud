package io.pillopl.acl;

import io.pillopl.acl.reconciliation.Reconciliation;
import io.pillopl.acl.toggles.NewModelToggles;
import io.pillopl.bigballofmud.dtos.BookDto;
import io.pillopl.newmodel.lending.application.LendingFacade;
import io.pillopl.newmodel.lending.application.readmodel.CollectedBooksView;
import io.pillopl.newmodel.lending.application.readmodel.PlacedOnHoldBooksView;
import io.pillopl.newmodel.lending.domain.patron.PatronId;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.togglz.junit.TogglzRule;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LendingAclTest {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(NewModelToggles.class);
    @Mock
    Reconciliation<BookDto> reconciliation;
    @Mock
    LendingFacade lendingFacade;
    @InjectMocks
    LendingACL lendingACL;

    UUID patronId = UUID.randomUUID();


    List<BookDto> oldModelResult = new ArrayList<>();
    PlacedOnHoldBooksView placedOnHoldBooksView =
            new PlacedOnHoldBooksView(new PatronId(patronId), new ArrayList<>());
    CollectedBooksView collectedBooksView =
            new CollectedBooksView(new PatronId(patronId), new ArrayList<>());

    List<BookDto> newModelPlacedOnHoldBooks = BookDto.translateFrom(placedOnHoldBooksView);
    List<BookDto> newModelCollectedBooks = BookDto.translateFrom(collectedBooksView);

    @Before
    public void setup() {
        oldModelResult.add(new BookDto());
        oldModelResult.add(new BookDto());
    }

    @After
    public void cleanup() {
        goodOldFashionedModelIsEnabled();
    }

    @Test
    public void shouldJustReconcileBooksPlacedOnHold() {
        //given
        justReconciliationIsEnabled();
        //and
        newModelReturnsNewResults();

        //when
        List<BookDto> books = askingForBooksPlacedOnHold();

        //then
        assertThat(books).isEqualTo(oldModelResult);
        //and
        reconciliationOfPlacedOnHoldBooksWasDone();
    }

    @Test
    public void shouldReturnNewModelForBooksPlacedOnHold() {
        //given
        newModelIsEnabled();

        //when
        List<BookDto> books = askingForBooksPlacedOnHold();

        //then
        assertThat(books).isEqualTo(newModelPlacedOnHoldBooks);
        //and
        reconciliationOfPlacedOnHoldBooksWasDone();
    }

    @Test
    public void shouldOldModelAndNotReconcileForBooksPlacedOnHold() {
        //given
        goodOldFashionedModelIsEnabled();

        //when
        List<BookDto> books = askingForBooksPlacedOnHold();

        //then
        assertThat(books).isEqualTo(oldModelResult);
        //and
        noReconciliationWasDone();
    }

    @Test
    public void shouldJustReconcileCollectedBooks() {
        //given
        justReconciliationIsEnabled();

        //when
        List<BookDto> books = askingForBooksPlacedOnHold();

        //then
        assertThat(books).isEqualTo(oldModelResult);
        //and
        reconciliationOfCollectedBooksWasDone();
    }

    @Test
    public void shouldReturnNewModelForCollectedBooks() {
        //given
        newModelIsEnabled();

        //when
        List<BookDto> books = askingForCollectedBooks();

        //then
        assertThat(books).isEqualTo(newModelCollectedBooks);
        //and
        reconciliationOfCollectedBooksWasDone();
    }

    @Test
    public void shouldOldModelAndNotReconcileForCollectedBooks() {
        //given
        goodOldFashionedModelIsEnabled();

        //when
        List<BookDto> books = askingForCollectedBooks();

        //then
        assertThat(books).isEqualTo(oldModelResult);
        //and
        noReconciliationWasDone();
    }

	@Test
	public void shouldUseOldModelEvenIfNewOneThrowsException() {
		//given
		justReconciliationIsEnabled();
		//and
		askingForCollectedBooksThrowsException();

		//when
		List<BookDto> books = askingForCollectedBooks();

		//then
		assertThat(books).isEqualTo(oldModelResult);
		//and
		noReconciliationWasDone();
	}

    void noReconciliationWasDone() {
        verifyZeroInteractions(reconciliation);
    }

    List<BookDto> askingForBooksPlacedOnHold() {
        return lendingACL.booksPlacedOnHoldBy(patronId, oldModelResult);
    }

    List<BookDto> askingForCollectedBooks() {
        return lendingACL.booksCurrentlyCollectedBy(patronId, oldModelResult);
    }

    void justReconciliationIsEnabled() {
        togglzRule.disable(NewModelToggles.RECONCILE_AND_USE_NEW_MODEL);
        togglzRule.enable(NewModelToggles.RECONCILE_NEW_MODEL);
        newModelReturnsNewResults();

    }

    void newModelIsEnabled() {
        togglzRule.enable(NewModelToggles.RECONCILE_AND_USE_NEW_MODEL);
        togglzRule.enable(NewModelToggles.RECONCILE_NEW_MODEL);
        newModelReturnsNewResults();
    }

    void goodOldFashionedModelIsEnabled() {
        togglzRule.disable(NewModelToggles.RECONCILE_AND_USE_NEW_MODEL);
        togglzRule.disable(NewModelToggles.RECONCILE_NEW_MODEL);
    }

	void askingForCollectedBooksThrowsException() {
		when(lendingFacade.booksCollectedBy(new PatronId(patronId))).thenThrow(new RuntimeException("New model collect exception!"));
	}

    void newModelReturnsNewResults() {

        when(lendingFacade.booksPlacedOnHoldBy(new PatronId(patronId))).thenReturn(placedOnHoldBooksView);
        when(lendingFacade.booksCollectedBy(new PatronId(patronId))).thenReturn(collectedBooksView);

    }

    void reconciliationOfPlacedOnHoldBooksWasDone() {
        verify(reconciliation).compare(toSet(oldModelResult), toSet(newModelPlacedOnHoldBooks));
    }

    void reconciliationOfCollectedBooksWasDone() {
        verify(reconciliation).compare(toSet(oldModelResult), toSet(newModelPlacedOnHoldBooks));
    }

    Set<BookDto> toSet(List<BookDto> books) {
        return new HashSet<>(books);
    }

}
