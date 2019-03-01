package io.pillopl.newmodel.lending.domain.patron

import io.pillopl.newmodel.lending.domain.book.AvailableBook
import io.pillopl.newmodel.lending.domain.patron.events.BookCollected
import spock.lang.Specification

import java.time.Period

import static io.pillopl.newmodel.lending.domain.patron.CollectDuration.forOneMonth
import static io.pillopl.newmodel.lending.domain.patron.Fixtures.aRegularPatron
import static io.pillopl.newmodel.lending.domain.patron.Fixtures.circulatingBook
import static io.pillopl.newmodel.lending.domain.patron.HoldDuration.forTenDays

class CollectingPlacedOnHoldBookScenarios extends Specification {

    /**
     * Task #2: Implement this test, make it pass.
     * Remember that Fixtures, for instance aRegularPatron() should be changed so that it returns a regular patron in the meaning of your new model.
     */
    def 'can collect an existing hold'() {
        given:
            Patron patron = aRegularPatron()
        and:
            AvailableBook book = circulatingBook()
        and:
            patron.placeOnHold(book, forTenDays())
        when:
            Optional<BookCollected> event = patron.collect(book.bookId, forOneMonth())
        then:
            event.isPresent()
    }

    def 'cannot collect when book is not on hold'() {
        given:
            Patron patron = aRegularPatron()
        and:
            AvailableBook book = circulatingBook()
        when:
            Optional<BookCollected> event = patron.collect(book.bookId, forOneMonth())
        then:
            !event.isPresent()
    }

    def 'collect duration can be less then or 60 days'() {
        when:
            aRegularPatron().collect(circulatingBook().bookId, forDays(days))
        then:
            thrown(IllegalArgumentException)
        where:
            days << (61..100)
    }

    CollectDuration forDays(int days) {
        return new CollectDuration(Period.ofDays(days))
    }

    def 'collect duration cannot be more than 60 days'() {
        when:
            aRegularPatron().collect(circulatingBook().bookId, forDays(days))
        then:
            noExceptionThrown()
        where:
            days << (1..60)
    }
}
