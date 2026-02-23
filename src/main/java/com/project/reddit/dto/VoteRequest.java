package com.project.reddit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {

    @NotNull
    private Integer voteValue; // +1 or -1
}