package com.choosr.choosr_service.model.dto;

import com.choosr.choosr_service.model.entity.DecisionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DecisionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private List<OptionViewDTO> options;
    private DecisionStatus status;
    private Instant closesAt;
    private Instant createdAt;
    private DecisionResultDTO result;
    /**
     * When true, the current device has already submitted a vote (used by the app to disable extra votes).
     */
    private boolean hasVoted;
    /**
     * While the decision is open: recent votes (newest first) for a live activity feed.
     */
    private List<VoteActivityDTO> voteActivity;
}
