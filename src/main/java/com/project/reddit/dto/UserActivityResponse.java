package com.project.reddit.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserActivityResponse {
    private String userId;
    private long totalPosts;
    private long totalComments;
}
