package com.choosr.choosr_service.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OptionEmbedded {
    private String id;
    private String label;

    public OptionEmbedded(String id, String label) {
        this.id = id;
        this.label = label;
    }
}
