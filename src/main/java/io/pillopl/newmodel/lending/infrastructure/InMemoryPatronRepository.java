package io.pillopl.newmodel.lending.infrastructure;

import io.pillopl.newmodel.lending.domain.patron.Patron;
import io.pillopl.newmodel.lending.domain.patron.PatronId;
import io.pillopl.newmodel.lending.domain.patron.PatronRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryPatronRepository implements PatronRepository {

    private final Map<PatronId, Patron> database = new HashMap<>();

    @Override
    public Optional<Patron> findById(PatronId patronId) {
        return Optional.ofNullable(database.get(patronId));
    }

    @Override
    public void save(Patron patron) {
        database.put(patron.getPatronId(), patron);
    }
}
