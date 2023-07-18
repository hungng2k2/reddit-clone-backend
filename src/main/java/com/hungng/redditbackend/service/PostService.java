package com.hungng.redditbackend.service;

import com.hungng.redditbackend.dto.PostRequest;
import com.hungng.redditbackend.dto.PostResponse;
import com.hungng.redditbackend.exception.RedditException;
import com.hungng.redditbackend.mapper.PostMapper;
import com.hungng.redditbackend.model.Post;
import com.hungng.redditbackend.model.Subreddit;
import com.hungng.redditbackend.model.User;
import com.hungng.redditbackend.repository.PostRepo;
import com.hungng.redditbackend.repository.SubredditRepo;
import com.hungng.redditbackend.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepo postRepo;
    private final SubredditRepo subredditRepo;
    private final UserRepo userRepo;
    private final PostMapper postMapper;
    private final AuthService authService;


    public PostResponse save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepo.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new RedditException("Subreddit not found with name: " + postRequest.getSubredditName()));
        User currentUser = authService.getCurrentUser();
        Post post = postMapper.map(postRequest, subreddit, currentUser);
        return postMapper.mapToDto(postRepo.save(post));
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        return postMapper.mapToDto(postRepo.findById(id)
                .orElseThrow(()->new RedditException("Post not found with id: "+id)));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepo.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long id) {
        Subreddit subreddit = subredditRepo.findById(id)
                .orElseThrow(() -> new RedditException("Subreddit not found with id: " + id));
        return postRepo.findAllBySubreddit(subreddit)
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<PostResponse>  getPostsByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(()->new RedditException("User not found with username: "+username));
        return postRepo.findAllByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
