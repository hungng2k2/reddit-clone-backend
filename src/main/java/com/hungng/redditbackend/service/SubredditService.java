package com.hungng.redditbackend.service;

import com.hungng.redditbackend.dto.SubredditDto;
import com.hungng.redditbackend.exception.RedditException;
import com.hungng.redditbackend.mapper.SubredditMapper;
import com.hungng.redditbackend.model.Subreddit;
import com.hungng.redditbackend.repository.SubredditRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepo subredditRepo;
    private final SubredditMapper subredditMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        if (subredditRepo.existsByName(subredditDto.getName())) {
            throw new RedditException("Subreddit already existed with name: " + subredditDto.getName());
        }
        Subreddit subreddit = subredditMapper.map(subredditDto);
        subredditRepo.save(subreddit);
        subredditDto.setId(subreddit.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepo.findAll()
                .stream()
                .map(subredditMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public SubredditDto getSubreddit(Long id) {
        return subredditMapper.mapToDto(subredditRepo.findById(id)
                .orElseThrow(() -> new RedditException("Subreddit not found with id: " + id)));
    }
}
