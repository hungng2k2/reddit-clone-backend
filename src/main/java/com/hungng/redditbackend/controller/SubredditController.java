package com.hungng.redditbackend.controller;

import com.hungng.redditbackend.dto.ResponseObject;
import com.hungng.redditbackend.dto.SubredditDto;
import com.hungng.redditbackend.service.SubredditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subreddit")
@RequiredArgsConstructor
@Slf4j
public class SubredditController {

    private final SubredditService subredditService;

    @PostMapping
    public ResponseEntity<SubredditDto> createSubreddit(@RequestBody SubredditDto subredditDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subredditService.save(subredditDto));
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<SubredditDto>>> getAll() {
        return ResponseEntity.ok(ResponseObject.<List<SubredditDto>>builder()
                .status(HttpStatus.OK)
                .message("")
                .data(subredditService.getAll())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubredditDto> getSubreddit(@PathVariable Long id) {
        return ResponseEntity.ok(subredditService.getSubreddit(id));
    }
}
