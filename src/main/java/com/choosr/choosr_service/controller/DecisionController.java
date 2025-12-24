package com.choosr.choosr_service.controller;

import com.choosr.choosr_service.model.dto.DecisionDTO;
import com.choosr.choosr_service.service.DecisionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DecisionController {
    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @GetMapping("/api/decisions")
    public List<DecisionDTO> getDecisions(){
        return decisionService.getDecisionsFromDB();
    }
}
