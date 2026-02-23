package com.project.reddit.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.index.Indexed;


@Document(collection = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    private String id;

    private String title;

    private String content;

    @Indexed
    private String subredditId;

    @Indexed
    private String authorId;

    private int voteCount;

    @Indexed
    private Instant createdAt;

    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}

