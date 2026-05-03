package com.choosr.choosr_service.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Anonymous vote event for live activity (no device identifier exposed).
 */
@Getter
@Setter
@NoArgsConstructor
public class VoteActivityDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String optionLabel;
    private Instant votedAt;
}
