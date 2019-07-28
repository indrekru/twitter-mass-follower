package com.ruubel.massfollower.controller;

import com.ruubel.massfollower.job.FollowJob;
import com.ruubel.massfollower.model.Followed;
import com.ruubel.massfollower.model.FollowingAmount;
import com.ruubel.massfollower.service.FollowService;
import com.ruubel.massfollower.service.FollowingAmountService;
import com.ruubel.massfollower.dao.FollowedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private FollowedRepository followPersistenceService;
    private FollowingAmountService followingAmountService;
    private FollowJob followJob;
    private FollowService followService;

    @Autowired
    public ApiController(
            FollowedRepository followPersistenceService,
            FollowingAmountService followingAmountService,
            FollowJob followJob,
            FollowService followService) {
        this.followPersistenceService = followPersistenceService;
        this.followingAmountService = followingAmountService;
        this.followJob = followJob;
        this.followService = followService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return new ResponseEntity(new HashMap<String, Object>(){{
            put("alive", true);
        }}, HttpStatus.OK);
    }

    @GetMapping("/followed")
    public ResponseEntity followed() {
        List<Followed> followed = followPersistenceService.findAll();
        return new ResponseEntity<>(followed, HttpStatus.OK);
    }

    @GetMapping("/follow-stats")
    public ResponseEntity stats() {
        Instant then = Instant.now().minusSeconds(2592000); // 30 days ago
        List<FollowingAmount> followingAmounts = followingAmountService.findByCreatedGreaterThanOrderByCreatedAsc(then);
        return new ResponseEntity<>(followingAmounts, HttpStatus.OK);
    }
}
