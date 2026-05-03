package com.choosr.choosr_service.controller;

import com.choosr.choosr_service.model.dto.CreateDecisionRequest;
import com.choosr.choosr_service.model.dto.DecisionDTO;
import com.choosr.choosr_service.model.dto.VoteRequest;
import com.choosr.choosr_service.service.DecisionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/decisions")
public class DecisionController {
    public static final String DEVICE_HEADER = "X-Device-Id";

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping
    public ResponseEntity<DecisionDTO> create(@Valid @RequestBody CreateDecisionRequest body) {
        DecisionDTO created = decisionService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public DecisionDTO get(@PathVariable String id,
                           @RequestHeader(value = DEVICE_HEADER, required = false) String deviceId) {
        return decisionService.getById(id, deviceId);
    }

    @PostMapping("/{id}/vote")
    public DecisionDTO vote(@PathVariable String id,
                            @Valid @RequestBody VoteRequest body,
                            @RequestHeader(value = DEVICE_HEADER, required = false) String deviceId) {
        return decisionService.vote(id, body, deviceId);
    }
}
