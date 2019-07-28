package com.ruubel.massfollower.job;

import com.ruubel.massfollower.model.Followed;
import com.ruubel.massfollower.dao.FollowedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class FollowedCleanupJob {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private int limit;

    private FollowedRepository followedRepository;

    @Autowired
    public FollowedCleanupJob(FollowedRepository followedRepository) {
        this.followedRepository = followedRepository;
        this.limit = 8500;
    }

//    @Scheduled(cron = "0 55 23 * * ?") // 23:59
    @Scheduled(cron = "0 0 0/3 * * ?") // Every 3 hours
    public List<Followed> cleanup() {
        log.info("Starting cleanup");

        List<Followed> followedList = followedRepository.findAll();

        if (followedList.size() > limit) {
            int elementsToRemove = followedList.size() - limit;
            Iterator<Followed> itr = followedList.iterator();
            List<Followed> removeElements = new ArrayList<>();
            int i = 0;
            while (itr.hasNext() && i < elementsToRemove) {
                Followed followed = itr.next();
                followedRepository.delete(followed);
                removeElements.add(followed);
                i++;
            }
            followedList.removeAll(removeElements);
        }

        log.info("Done with the cleanup");
        return followedList;
    }
}
