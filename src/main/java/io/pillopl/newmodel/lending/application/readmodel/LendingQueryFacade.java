package io.pillopl.newmodel.lending.application.readmodel;

import io.pillopl.newmodel.lending.domain.patron.PatronId;
import io.pillopl.newmodel.lending.domain.patron.events.BookCollected;
import io.pillopl.newmodel.lending.domain.patron.events.BookPlacedOnHold;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class LendingQueryFacade {

    private final Map<PatronId, PlacedOnHoldBooksView> holds = new HashMap<>();
    private final Map<PatronId, CollectedBooksView> collectedBooks = new HashMap<>();

    @EventListener
    public void placedOnHold(BookPlacedOnHold bookPlacedOnHold) {
        addBookToHoldBooksView(bookPlacedOnHold);
    }

    @EventListener
    public void bookCollected(BookCollected bookCollected) {
        addBookToCollectedBooksView(bookCollected);
        removeBookFromHoldView(bookCollected);
    }

    public PlacedOnHoldBooksView placedOnHoldBy(PatronId patronId) {
        return holds.get(patronId);
    }

    public CollectedBooksView collectedBy(PatronId patronId) {
        return collectedBooks.get(patronId);
    }

    private void addBookToHoldBooksView(BookPlacedOnHold bookPlacedOnHold) {
        PlacedOnHoldBooksView view = holds.getOrDefault(bookPlacedOnHold.getPatronId(), new PlacedOnHoldBooksView(bookPlacedOnHold.getPatronId()));
        view.addBook(bookPlacedOnHold.getBookId());
        holds.put(bookPlacedOnHold.getPatronId(), view);
    }

    private void removeBookFromHoldView(BookCollected bookCollected) {
        holds.get(bookCollected.getPatronId()).removeBook(bookCollected.getBookId());
    }

    private void addBookToCollectedBooksView(BookCollected bookCollected) {
        CollectedBooksView view = collectedBooks.getOrDefault(bookCollected.getPatronId(), new CollectedBooksView(bookCollected.getPatronId()));
        view.addBook(bookCollected.getBookId());
        collectedBooks.put(bookCollected.getPatronId(), view);
    }

}
