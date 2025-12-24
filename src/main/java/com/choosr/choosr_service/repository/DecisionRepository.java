package com.choosr.choosr_service.repository;

import com.choosr.choosr_service.model.entity.DecisionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DecisionRepository extends MongoRepository<DecisionEntity, String> {
}
