package com.project.reddit.presentation.controller;

import com.project.reddit.dto.*;
import com.project.reddit.service.PostService;
import com.project.reddit.migration.OracleToMongoMigrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final OracleToMongoMigrationService migrationService;

    @PostMapping
    public PostResponse createPost(@Valid @RequestBody CreatePostRequest request) {
        return postService.createPost(request);
    }

    @GetMapping("/{id}")
    public PostResponse getPost(@PathVariable String id) {
        return postService.getPostById(id);
    }

    @PostMapping("/{id}/comments")
    public PostResponse addComment(
            @PathVariable String id,
            @Valid @RequestBody AddCommentRequest request) {

        return postService.addComment(id, request);
    }

    @PatchMapping("/{id}/vote")
    public PostResponse votePost(
            @PathVariable String id,
            @RequestBody VoteRequest request) {

        return postService.votePost(id, request.getVoteValue());
    }

    @PatchMapping("/{postId}/comments/{commentId}/vote")
    public PostResponse voteComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            @RequestBody VoteRequest request) {

        return postService.voteComment(postId, commentId, request.getVoteValue());
    }

    @GetMapping("/users/{userId}/activity")
    public UserActivityResponse getUserActivity(@PathVariable String userId) {
        return postService.getUserActivity(userId);
    }

    @GetMapping("/migration/run")
    public String runMigration() {
        migrationService.migrate();
        return "Migration Completed";
    }
}