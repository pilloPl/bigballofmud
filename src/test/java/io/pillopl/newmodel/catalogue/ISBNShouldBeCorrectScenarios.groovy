package io.pillopl.newmodel.catalogue

import spock.lang.Specification

class ISBNShouldBeCorrectScenarios extends Specification {


    def "isbn should be trimmed"() {
        when:
            ISBN isbn = new ISBN("  1234123414  ")
        then:
            isbn.isbn == "1234123414"
    }

    def "isbn should match isbn10 regex"() {
        when:
            new ISBN("not isbn")
        then:
            thrown(IllegalArgumentException)
    }
}


