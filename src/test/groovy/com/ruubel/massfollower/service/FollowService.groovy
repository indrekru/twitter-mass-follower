package com.ruubel.massfollower.service

import com.ruubel.massfollower.config.ConfigParams
import com.ruubel.massfollower.dao.FollowedRepository
import spock.lang.Specification

class FollowServiceSpec extends Specification {

    FollowService service
    FollowedRepository followedRepository
    FollowingAmountService followingAmountService
    MailingService mailingService
    ConfigParams configParams

    def setup () {
        followedRepository = Mock(FollowedRepository)
        followingAmountService = Mock(FollowingAmountService)
        mailingService = Mock(MailingService)
        configParams = Mock(ConfigParams)
        service = new FollowService(followedRepository, followingAmountService, mailingService, configParams)
    }

    def "when, then" () {
        when:
            int result = service.add(1, 2)
        then:
            result == 3
    }

}