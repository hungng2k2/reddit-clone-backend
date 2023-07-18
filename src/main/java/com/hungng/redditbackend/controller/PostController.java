package com.hungng.redditbackend.controller;

import com.hungng.redditbackend.dto.PostRequest;
import com.hungng.redditbackend.dto.PostResponse;
import com.hungng.redditbackend.dto.ResponseObject;
import com.hungng.redditbackend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ResponseObject<PostResponse>> createPost(@RequestBody PostRequest postRequest){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.<PostResponse>builder()
                        .status(HttpStatus.CREATED)
                        .message("post created successfully")
                        .data(postService.save(postRequest))
                        .build());
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<PostResponse>>> getAllPosts() {
        return ResponseEntity.ok(ResponseObject.<List<PostResponse>>builder()
                .status(HttpStatus.OK)
                .message("post retrieved successfully")
                .data(postService.getAllPosts())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<PostResponse>> getPost(@PathVariable Long id){
        return ResponseEntity.ok(ResponseObject.<PostResponse>builder()
                .status(HttpStatus.OK)
                .message("post retrieved successfully")
                .data(postService.getPost(id))
                .build());
    }

    @GetMapping("/by-subreddit/{id}")
    public ResponseEntity<ResponseObject<List<PostResponse>>> getPostsBySubreddit(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseObject.<List<PostResponse>>builder()
                .status(HttpStatus.OK)
                .message("post retrieved successfully")
                .data(postService.getPostsBySubreddit(id))
                .build());
    }

    @GetMapping("/by-user/{username}")
    public ResponseEntity<ResponseObject<List<PostResponse>>> getPostsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(ResponseObject.<List<PostResponse>>builder()
                .status(HttpStatus.OK)
                .message("post retrieved successfully")
                .data(postService.getPostsByUsername(username))
                .build());
    }
}
