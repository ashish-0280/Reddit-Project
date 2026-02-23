package com.project.reddit.exception;

public class InvalidVoteException extends RuntimeException {

    public InvalidVoteException() {
        super("Vote must be either +1 or -1");
    }
}