package com.project.reddit.service;

import com.mongodb.client.AggregateIterable;
import com.project.reddit.dto.*;
import com.project.reddit.model.Comment;
import com.project.reddit.model.Post;
import com.project.reddit.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import com.project.reddit.exception.PostNotFoundException;
import com.project.reddit.exception.CommentNotFoundException;
import com.project.reddit.exception.InvalidVoteException;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Arrays;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final MongoTemplate mongoTemplate;
    private final PostRepository postRepository;

    @Override
    public PostResponse votePost(String postId, int voteValue) {

        if (voteValue != 1 && voteValue != -1) {
            throw new InvalidVoteException();
        }

        Query query = new Query(where("_id").is(postId));
        Update update = new Update().inc("voteCount", voteValue);

        mongoTemplate.updateFirst(query, update, Post.class);

        Post updated = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return mapToResponse(updated);
    }

    @Override
    public PostResponse voteComment(String postId, String commentId, int voteValue) {

        if (voteValue != 1 && voteValue != -1) {
            throw new InvalidVoteException();
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        boolean updated = updateCommentVoteRecursive(post.getComments(), commentId, voteValue);

        if (!updated) {
            throw new CommentNotFoundException(commentId);
        }

        Post saved = postRepository.save(post);

        return mapToResponse(saved);
    }

    private boolean updateCommentVoteRecursive(
            java.util.List<Comment> comments,
            String commentId,
            int voteValue) {

        for (Comment comment : comments) {

            if (comment.getCommentId().equals(commentId)) {
                comment.setVoteCount(comment.getVoteCount() + voteValue);
                return true;
            }

            if (updateCommentVoteRecursive(comment.getReplies(), commentId, voteValue)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public PostResponse addComment(String postId, AddCommentRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Comment newComment = Comment.builder()
                .commentId(java.util.UUID.randomUUID().toString())
                .authorId(request.getAuthorId())
                .body(request.getBody())
                .voteCount(0)
                .createdAt(java.time.Instant.now())
                .build();

        if (request.getParentCommentId() == null) {
            post.getComments().add(newComment);
        } else {
            boolean added = addReplyRecursive(post.getComments(),
                    request.getParentCommentId(),
                    newComment);

            if (!added) {
                throw new RuntimeException("Parent comment not found");
            }
        }

        Post saved = postRepository.save(post);

        return mapToResponse(saved);
    }

    @Override
    public UserActivityResponse getUserActivity(String userId) {

        List<Document> pipeline = Arrays.asList(

                // Stage 1: $match
                new Document("$match",
                        new Document("$or", Arrays.asList(
                                new Document("authorId", userId),
                                new Document("comments.authorId", userId)
                        ))
                ),

                // Stage 2: $unwind comments
                new Document("$unwind",
                        new Document("path", "$comments")
                                .append("preserveNullAndEmptyArrays", true)
                ),

                // Stage 3: $group
                new Document("$group",
                        new Document("_id", null)
                                .append("totalPosts",
                                        new Document("$sum",
                                                new Document("$cond", Arrays.asList(
                                                        new Document("$eq", Arrays.asList("$authorId", userId)),
                                                        1,
                                                        0
                                                ))
                                        )
                                )
                                .append("totalComments",
                                        new Document("$sum",
                                                new Document("$cond", Arrays.asList(
                                                        new Document("$eq", Arrays.asList("$comments.authorId", userId)),
                                                        1,
                                                        0
                                                ))
                                        )
                                )
                )
        );

        AggregateIterable<Document> result =
                mongoTemplate.getCollection("posts").aggregate(pipeline);

        Document doc = result.first();

        long totalPosts = 0;
        long totalComments = 0;

        if (doc != null) {
            totalPosts = doc.getInteger("totalPosts", 0);
            totalComments = doc.getInteger("totalComments", 0);
        }

        return UserActivityResponse.builder()
                .userId(userId)
                .totalPosts(totalPosts)
                .totalComments(totalComments)
                .build();
    }

    private boolean addReplyRecursive(
            java.util.List<Comment> comments,
            String parentId,
            Comment newComment) {

        for (Comment comment : comments) {

            if (comment.getCommentId().equals(parentId)) {
                comment.getReplies().add(newComment);
                return true;
            }

            if (addReplyRecursive(comment.getReplies(), parentId, newComment)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public PostResponse createPost(CreatePostRequest request) {

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .subredditId(request.getSubredditId())
                .authorId(request.getAuthorId())
                .voteCount(0)
                .createdAt(Instant.now())
                .build();

        Post saved = postRepository.save(post);

        return mapToResponse(saved);
    }

    @Override
    public PostResponse getPostById(String postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return mapToResponse(post);
    }

    private PostResponse mapToResponse(Post post) {

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .subredditId(post.getSubredditId())
                .authorId(post.getAuthorId())
                .voteCount(post.getVoteCount())
                .createdAt(post.getCreatedAt())
                .comments(
                        post.getComments().stream()
                                .map(this::mapComment)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private CommentResponse mapComment(Comment comment) {

        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .authorId(comment.getAuthorId())
                .body(comment.getBody())
                .voteCount(comment.getVoteCount())
                .createdAt(comment.getCreatedAt())
                .replies(
                        comment.getReplies().stream()
                                .map(this::mapComment)
                                .collect(Collectors.toList())
                )
                .build();
    }
}