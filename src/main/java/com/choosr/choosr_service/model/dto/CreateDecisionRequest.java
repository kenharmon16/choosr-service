package com.choosr.choosr_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class CreateDecisionRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    @NotEmpty
    @Size(min = 2, max = 20)
    private List<@NotBlank @Size(max = 120) String> options;

    private Instant closesAt;
}
