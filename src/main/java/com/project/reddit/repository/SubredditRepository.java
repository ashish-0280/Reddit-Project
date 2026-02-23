package com.project.reddit.repository;

import com.project.reddit.model.Subreddit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubredditRepository extends MongoRepository<Subreddit, String> {

    Optional<Subreddit> findByName(String name);
}