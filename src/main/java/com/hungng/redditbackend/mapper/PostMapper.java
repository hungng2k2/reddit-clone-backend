package com.hungng.redditbackend.mapper;

import com.hungng.redditbackend.dto.PostRequest;
import com.hungng.redditbackend.dto.PostResponse;
import com.hungng.redditbackend.model.*;
import com.hungng.redditbackend.repository.CommentRepo;
import com.hungng.redditbackend.repository.VoteRepo;
import com.hungng.redditbackend.service.AuthService;
import com.hungng.redditbackend.utils.TimeAgo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class PostMapper {
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private VoteRepo voteRepo;
    @Autowired
    private AuthService authService;

    @Mapping(target = "id", source = "postRequest.id")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "voteCount", constant = "0")
    @Mapping(target = "user", source = "user")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
    @Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    @Mapping(target = "commentCount", expression = "java(getCommentCount(post))")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "subredditName", source = "subreddit.name")
    public abstract PostResponse mapToDto(Post post);

    Integer getCommentCount(Post post) {
        return commentRepo.findAllByPost(post).size();
    }

    String getDuration(Post post) {
        return TimeAgo.toDuration(Instant.now().toEpochMilli() - post.getCreatedDate().toEpochMilli());
    }

    boolean isPostUpVoted(Post post) {
        return checkVoteType(post, VoteType.UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, VoteType.DOWNVOTE);
    }

    private boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isLoggedIn()) {
            Optional<Vote> voteForPostByUser = voteRepo.findTopByPostAndUserOrderByIdDesc(post, authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
        }
        return false;
    }
}
