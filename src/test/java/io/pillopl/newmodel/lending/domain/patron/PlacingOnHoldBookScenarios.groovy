package io.pillopl.newmodel.lending.domain.patron

import io.pillopl.newmodel.lending.domain.patron.events.BookPlacedOnHold
import spock.lang.Specification

import static io.pillopl.newmodel.lending.domain.patron.Fixtures.circulatingBook
import static io.pillopl.newmodel.lending.domain.patron.Fixtures.restrictedBook
import static io.pillopl.newmodel.lending.domain.patron.HoldDuration.forTenDays

class PlacingOnHoldBookScenarios extends Specification {

    def 'can place on hold circulating book if patron is regular'() {
        given:
            Patron patron = Fixtures.aRegularPatron()
        when:
            Optional<BookPlacedOnHold> event = patron.placeOnHold(circulatingBook(), forTenDays())
        then:
            event.isPresent()
    }

    def 'cannot place on hold restricted book if patron is regular'() {
        given:
            Patron patron = Fixtures.aRegularPatron()
        when:
            Optional<BookPlacedOnHold> event = patron.placeOnHold(restrictedBook(), forTenDays())
        then:
            !event.isPresent()
    }

    def 'can place on hold restricted book if patron is researcher'() {
        given:
            Patron patron = Fixtures.aResearcherPatron()
        when:
            Optional<BookPlacedOnHold> event = patron.placeOnHold(restrictedBook(), forTenDays())
        then:
            event.isPresent()
    }

    def 'can place on hold up to 5 books'() {
        given:
            Patron patron = Fixtures.aResearcherPatron()
        and:
            5.times { patron.placeOnHold(circulatingBook(), forTenDays())}
        when:
            Optional<BookPlacedOnHold> event = patron.placeOnHold(circulatingBook(), forTenDays())
        then:
            !event.isPresent()
    }

    def 'cannot place on hold when there are 2 overdue collected books'() {
        given:
            Patron patron = Fixtures.aResearcherPatronWithTwoOverdueBooks()
        when:
            Optional<BookPlacedOnHold> event = patron.placeOnHold(circulatingBook(), forTenDays())
        then:
            !event.isPresent()
    }

    def 'cannot place open-ended hold if patron is regular'() {
        given:
            Patron patron = Fixtures.aRegularPatron()
        when:
            Optional<BookPlacedOnHold> event = patron.placeOnHold(circulatingBook(), new OpenEndedHoldDuration())
        then:
            !event.isPresent()
    }

    def 'can place open-ended hold if patron is researcher'() {
        given:
            Patron patron = Fixtures.aResearcherPatron()
        when:
            Optional<BookPlacedOnHold> event = patron.placeOnHold(circulatingBook(), new OpenEndedHoldDuration())
        then:
            event.isPresent()
    }
}
