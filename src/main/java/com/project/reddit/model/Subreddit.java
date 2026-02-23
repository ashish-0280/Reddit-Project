package com.project.reddit.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "subreddits")
public class Subreddit {

    @Id
    private String id;
    @Indexed(unique = true)
    private String name;

    private String description;
}