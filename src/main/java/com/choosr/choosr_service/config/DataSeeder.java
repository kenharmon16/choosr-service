package com.choosr.choosr_service.config;

import com.choosr.choosr_service.model.entity.DecisionEntity;
import com.choosr.choosr_service.model.entity.DecisionStatus;
import com.choosr.choosr_service.model.entity.OptionEmbedded;
import com.choosr.choosr_service.repository.DecisionRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@Profile("!test")
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(DecisionRepository decisionRepository, ObjectMapper objectMapper) {
        return args -> {
            if (decisionRepository.count() > 0) {
                return;
            }
            InputStream inputStream = new ClassPathResource("json/decisions.json").getInputStream();
            List<SeedDecision> seeds = objectMapper.readValue(inputStream, new TypeReference<>() {});

            List<DecisionEntity> toSave = new ArrayList<>();
            for (SeedDecision s : seeds) {
                DecisionEntity d = new DecisionEntity();
                d.setTitle(s.getTitle());
                d.setCreatedAt(Instant.now());
                d.setStatus(DecisionStatus.OPEN);
                d.setClosesAt(null);
                d.setVotes(new ArrayList<>());

                List<OptionEmbedded> opts = new ArrayList<>();
                if (s.getOptions() != null) {
                    for (SeedOption o : s.getOptions()) {
                        opts.add(new OptionEmbedded(UUID.randomUUID().toString(), o.getLabel()));
                    }
                }
                d.setOptions(opts);
                toSave.add(d);
            }

            decisionRepository.saveAll(toSave);
            System.out.println("MongoDB seeded with " + toSave.size() + " decision(s)");
        };
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SeedDecision {
        private String title;
        private List<SeedOption> options;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SeedOption {
        private String label;
    }
}
