package com.project.reddit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PostResponse {

    private String id;
    private String title;
    private String content;
    private String subredditId;
    private String authorId;
    private int voteCount;
    private Instant createdAt;
    private List<CommentResponse> comments;
}