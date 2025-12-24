package com.choosr.choosr_service.service;

import com.choosr.choosr_service.model.dto.DecisionDTO;
import com.choosr.choosr_service.repository.DecisionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DecisionService {
    private final DecisionRepository decisionRepository;
    private final ObjectMapper objectMapper;

    public DecisionService(DecisionRepository decisionRepository, ObjectMapper objectMapper) {
        this.decisionRepository = decisionRepository;
        this.objectMapper = objectMapper;
    }

    public List<DecisionDTO> getDecisionsFromDB(){
        return objectMapper.convertValue(decisionRepository.findAll(), new TypeReference<List<DecisionDTO>>() {});
    }
}
