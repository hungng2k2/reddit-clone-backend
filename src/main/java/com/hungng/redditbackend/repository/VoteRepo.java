package com.hungng.redditbackend.repository;

import com.hungng.redditbackend.model.Post;
import com.hungng.redditbackend.model.User;
import com.hungng.redditbackend.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepo extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByIdDesc(Post post, User currentUser);
}
