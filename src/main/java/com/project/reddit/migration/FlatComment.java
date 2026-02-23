package com.project.reddit.migration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlatComment {

    private String commentId;
    private String postId;
    private String authorId;
    private String parentId; // null if top-level
    private String body;
}