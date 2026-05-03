package com.choosr.choosr_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionViewDTO {
    private String id;
    private String label;
    /** Vote count for this option (live tallies while open and final counts when closed). */
    private Integer voteCount;
}
