package com.hungng.redditbackend.repository;

import com.hungng.redditbackend.model.Post;
import com.hungng.redditbackend.model.Subreddit;
import com.hungng.redditbackend.model.User;
import com.hungng.redditbackend.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findAllByUser(User user);
}
