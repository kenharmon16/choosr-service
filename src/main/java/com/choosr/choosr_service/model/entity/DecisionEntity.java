package com.choosr.choosr_service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "decisions")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecisionEntity {
    @Id
    public String id;
    public String title;
    public String description;
}
