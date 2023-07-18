package com.hungng.redditbackend.service;

import com.hungng.redditbackend.dto.VoteDto;
import com.hungng.redditbackend.exception.RedditException;
import com.hungng.redditbackend.model.Post;
import com.hungng.redditbackend.model.Vote;
import com.hungng.redditbackend.model.VoteType;
import com.hungng.redditbackend.repository.PostRepo;
import com.hungng.redditbackend.repository.VoteRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final VoteRepo voteRepo;
    private final PostRepo postRepo;
    private final AuthService authService;

    public void vote(VoteDto voteDto) {
        Post post = postRepo.findById(voteDto.getPostId())
                .orElseThrow(() -> new RedditException("Post not found with id: " + voteDto.getPostId()));
        Optional<Vote> voteByPostAndUser = voteRepo.findTopByPostAndUserOrderByIdDesc(post
                , authService.getCurrentUser());
        if (voteByPostAndUser.isPresent()) {
            Vote vote = voteByPostAndUser.get();
            //unvote
            if (vote.getVoteType().equals(voteDto.getVoteType())) {
                post.setVoteCount((post.getVoteCount()) - voteDto.getVoteType().getValue());
                voteRepo.delete(vote);
            }
            //reverse
            else {
                post.setVoteCount(post.getVoteCount() - vote.getVoteType().getValue() + voteDto.getVoteType().getValue());
                vote.setVoteType(voteDto.getVoteType());
                voteRepo.save(vote);
            }
        }
        //new vote
        else {
            post.setVoteCount(post.getVoteCount() + voteDto.getVoteType().getValue());
            voteRepo.save(mapToVote(voteDto, post));
        }
        postRepo.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
