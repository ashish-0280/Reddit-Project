package com.project.reddit.migration;

import com.project.reddit.model.Comment;
import com.project.reddit.model.Post;
import com.project.reddit.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OracleToMongoMigrationService {

    private final PostRepository postRepository;

    public void migrate() {

        //Simulate Oracle flat comment table
        List<FlatComment> flatComments = Arrays.asList(
                new FlatComment("1", "post1", "user1", null, "Top comment"),
                new FlatComment("2", "post1", "user2", "1", "Reply to 1"),
                new FlatComment("3", "post1", "user3", "2", "Reply to 2")
        );

        //Map to store commentId -> Comment object
        Map<String, Comment> commentMap = new HashMap<>();

        //List for top-level comments
        List<Comment> rootComments = new ArrayList<>();

        //First pass: create all Comment objects
        for (FlatComment flat : flatComments) {

            Comment comment = Comment.builder()
                    .commentId(flat.getCommentId())
                    .authorId(flat.getAuthorId())
                    .body(flat.getBody())
                    .voteCount(0)
                    .createdAt(Instant.now())
                    .build();

            commentMap.put(flat.getCommentId(), comment);
        }

        //Second pass: build hierarchy
        for (FlatComment flat : flatComments) {

            Comment current = commentMap.get(flat.getCommentId());

            if (flat.getParentId() == null) {
                // Top-level comment
                rootComments.add(current);
            } else {
                // Reply → attach to parent
                Comment parent = commentMap.get(flat.getParentId());
                parent.getReplies().add(current);
            }
        }

        //Create final Mongo Post document
        Post post = Post.builder()
                .id("post1")
                .title("Migrated Post")
                .content("Content from Oracle")
                .authorId("user1")
                .subredditId("learnprogramming")
                .voteCount(0)
                .createdAt(Instant.now())
                .comments(rootComments)
                .build();

        //Save into MongoDB
        postRepository.save(post);
    }
}