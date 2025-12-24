package com.choosr.choosr_service.config;

import com.choosr.choosr_service.model.entity.DecisionEntity;
import com.choosr.choosr_service.repository.DecisionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(DecisionRepository decisionRepository, ObjectMapper objectMapper){
        return args -> {
            if (decisionRepository.count() > 0){
                return;
            }
            InputStream inputStream = new ClassPathResource("json/decisions.json").getInputStream();
            List<DecisionEntity> decisionEntityList =
                    objectMapper.readValue(inputStream, new TypeReference<List<DecisionEntity>>() {});

            decisionRepository.saveAll(decisionEntityList);
            System.out.println("MongoDB seeded with decision data");
        };
    }
}
