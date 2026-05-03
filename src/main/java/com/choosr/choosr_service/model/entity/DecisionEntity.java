package com.choosr.choosr_service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "decisions")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecisionEntity {
    @Id
    private String id;
    private String title;
    private List<OptionEmbedded> options = new ArrayList<>();
    private DecisionStatus status = DecisionStatus.OPEN;
    private Instant closesAt;
    private Instant createdAt;
    private List<VoteRecord> votes = new ArrayList<>();
}
