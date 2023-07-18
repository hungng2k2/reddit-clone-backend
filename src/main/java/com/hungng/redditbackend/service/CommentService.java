package com.hungng.redditbackend.service;

import com.hungng.redditbackend.dto.CommentDto;
import com.hungng.redditbackend.dto.NotificationEmail;
import com.hungng.redditbackend.exception.RedditException;
import com.hungng.redditbackend.mapper.CommentMapper;
import com.hungng.redditbackend.model.Comment;
import com.hungng.redditbackend.model.Post;
import com.hungng.redditbackend.model.User;
import com.hungng.redditbackend.repository.CommentRepo;
import com.hungng.redditbackend.repository.PostRepo;
import com.hungng.redditbackend.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private static final String POST_URL = "";
    private final CommentRepo commentRepo;
    private final UserRepo userRepo;
    private final PostRepo postRepo;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final MailService mailService;

    public CommentDto save(CommentDto commentDto) {
        Post post = postRepo.findById(commentDto.getPostId())
                .orElseThrow(() -> new RedditException("Post not found with id: " + commentDto.getPostId()));
        User commentOwner = authService.getCurrentUser();
        User postOwner = post.getUser();
        Comment comment = commentMapper.map(commentDto, post, commentOwner);
        commentRepo.save(comment);
        sendCommentNotification(commentOwner, postOwner);
        return commentMapper.mapToDto(comment);
    }

    public List<CommentDto> getAllCommentsForPost(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RedditException("Post not found with id: " + postId));
        return commentRepo.findAllByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getCommentsByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(()-> new RedditException("User not found with username: "+username));
        return commentRepo.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private void sendCommentNotification(User commentOwner, User postOwner) {
        mailService.sendMail(NotificationEmail.builder()
                .subject(commentOwner.getUsername() + " commented on your post")
                .recipient(postOwner.getEmail())
                .body(postOwner.getUsername() + " posted a comment on your post." + POST_URL)
                .build());
    }

}
