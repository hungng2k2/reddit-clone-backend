package com.hungng.redditbackend.repository;

import com.hungng.redditbackend.dto.CommentDto;
import com.hungng.redditbackend.model.Comment;
import com.hungng.redditbackend.model.Post;
import com.hungng.redditbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);

    List<Comment> findAllByUser(User user);
}
