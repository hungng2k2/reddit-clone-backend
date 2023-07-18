package com.hungng.redditbackend.model;

public enum VoteType {
    UPVOTE(1), DOWNVOTE(-1)
    ;
    int value;
    VoteType(int direction) {
        value = direction;
    }

    public int getValue() {
        return value;
    }
}
