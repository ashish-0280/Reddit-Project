package com.project.reddit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class CommentResponse {

    private String commentId;
    private String authorId;
    private String body;
    private int voteCount;
    private Instant createdAt;
    private List<CommentResponse> replies;
}