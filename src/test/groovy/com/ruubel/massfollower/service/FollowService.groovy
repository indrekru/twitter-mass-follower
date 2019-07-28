package com.ruubel.massfollower.service

import spock.lang.Specification

class FollowServiceSpec extends Specification {

    FollowService service

    def setup () {
        service = new FollowService()
    }

    def "when, then" () {
        when:
            int result = service.add(1, 2)
        then:
            result == 3
    }

}