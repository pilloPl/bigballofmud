package io.pillopl.newmodel.catalogue

import spock.lang.Specification

class AuthorNameShouldBeCorrectScenarios extends Specification {

    def "author should not be null"() {
        when:
            new Author(null)
        then:
            thrown(NullPointerException)
    }


    def "author should not be longer than 60"() {
        when:
            new Author("Very Long Name, Perhaps one taken after mother and another after father. We don't support it.")
        then:
            thrown(IllegalArgumentException)
    }

}


