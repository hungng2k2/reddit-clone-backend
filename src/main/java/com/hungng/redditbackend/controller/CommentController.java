package com.hungng.redditbackend.controller;

import com.hungng.redditbackend.dto.CommentDto;
import com.hungng.redditbackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentDto commentDto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.save(commentDto));
    }

    @GetMapping("/by-postId/{postId}")
    public ResponseEntity<?> getAllCommentsForPost(@PathVariable Long postId){
        return ResponseEntity.ok(commentService.getAllCommentsForPost(postId));
    }

    @GetMapping("/by-user/{username}")
    public ResponseEntity<?> getCommentsByUsername(@PathVariable String username){
        return ResponseEntity.ok(commentService.getCommentsByUsername(username));
    }

}
