package com.choosr.choosr_service.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class VoteRecord {
    private String optionId;
    private String deviceId;
    private Instant votedAt;

    public VoteRecord(String optionId, String deviceId, Instant votedAt) {
        this.optionId = optionId;
        this.deviceId = deviceId;
        this.votedAt = votedAt;
    }
}
