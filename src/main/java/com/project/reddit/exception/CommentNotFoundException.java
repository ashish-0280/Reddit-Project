package com.project.reddit.exception;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(String commentId) {
        super("Comment not found with id: " + commentId);
    }
}