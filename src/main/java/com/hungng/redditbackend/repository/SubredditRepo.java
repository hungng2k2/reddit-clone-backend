package com.hungng.redditbackend.repository;

import com.hungng.redditbackend.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubredditRepo extends JpaRepository<Subreddit, Long> {
    Optional<Subreddit> findByName(String name);

    Boolean existsByName(String name);
}
