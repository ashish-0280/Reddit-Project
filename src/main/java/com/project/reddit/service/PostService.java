package com.project.reddit.service;

import com.project.reddit.dto.AddCommentRequest;
import com.project.reddit.dto.CreatePostRequest;
import com.project.reddit.dto.PostResponse;
import com.project.reddit.dto.UserActivityResponse;

public interface PostService {

    PostResponse votePost(String postId, int voteValue);

    PostResponse createPost(CreatePostRequest request);

    PostResponse getPostById(String postId);

    PostResponse addComment(String postId, AddCommentRequest request);

    PostResponse voteComment(String postId, String commentId, int voteValue);

    UserActivityResponse getUserActivity(String userId);

}