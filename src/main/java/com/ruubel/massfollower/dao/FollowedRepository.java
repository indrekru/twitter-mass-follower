package com.ruubel.massfollower.dao;

import com.ruubel.massfollower.model.Followed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FollowedRepository extends JpaRepository<Followed, Long> {
    Followed findByExternalId(String externalId);
    List<Followed> findByFollowedLessThan(Instant then);
}
