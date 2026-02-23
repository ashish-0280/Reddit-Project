package com.project.reddit.model;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    private String commentId;

    private String authorId;

    private String body;

    private int voteCount;

    private Instant createdAt;

    @Builder.Default
    private List<Comment> replies = new ArrayList<>();
}