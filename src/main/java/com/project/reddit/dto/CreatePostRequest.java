package com.project.reddit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private String subredditId;

    @NotBlank
    private String authorId;
}