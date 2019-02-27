package io.pillopl.newmodel.catalogue

import spock.lang.Specification

class TitleShouldBeCorrectScenarios extends Specification {

    def "title should not be null"() {
        when:
            new Title(null)
        then:
            thrown(NullPointerException)
    }

    def "title should not be longer than 100"() {
        when:
            new Title("Very Long Title Very Long Title Very Long Title Very Long Title Very Long Title Very Long Title Very Very")
        then:
            thrown(IllegalArgumentException)
    }

}


