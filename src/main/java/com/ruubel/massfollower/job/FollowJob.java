package com.ruubel.massfollower.job;

import com.ruubel.massfollower.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FollowJob {

    private FollowService followService;

    @Autowired
    public FollowJob(FollowService followService) {
        this.followService = followService;
    }

    @Scheduled(cron = "0 0/10 * * * ?") // Every 10 minutes
    public void run() throws Exception {
        followService.execute();
    }
}
