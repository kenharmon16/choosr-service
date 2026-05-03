package com.choosr.choosr_service.service;

import com.choosr.choosr_service.exception.ApiException;
import com.choosr.choosr_service.model.dto.CreateDecisionRequest;
import com.choosr.choosr_service.model.dto.DecisionDTO;
import com.choosr.choosr_service.model.dto.DecisionResultDTO;
import com.choosr.choosr_service.model.dto.OptionViewDTO;
import com.choosr.choosr_service.model.dto.VoteActivityDTO;
import com.choosr.choosr_service.model.dto.VoteRequest;
import com.choosr.choosr_service.model.entity.DecisionEntity;
import com.choosr.choosr_service.model.entity.DecisionStatus;
import com.choosr.choosr_service.model.entity.OptionEmbedded;
import com.choosr.choosr_service.model.entity.VoteRecord;
import com.choosr.choosr_service.repository.DecisionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DecisionService {

    private final DecisionRepository decisionRepository;

    public DecisionService(DecisionRepository decisionRepository) {
        this.decisionRepository = decisionRepository;
    }

    public DecisionDTO create(CreateDecisionRequest request) {
        DecisionEntity entity = new DecisionEntity();
        entity.setTitle(request.getTitle().trim());
        entity.setCreatedAt(Instant.now());
        entity.setStatus(DecisionStatus.OPEN);
        entity.setClosesAt(request.getClosesAt());

        List<OptionEmbedded> options = new ArrayList<>();
        for (String label : request.getOptions()) {
            options.add(new OptionEmbedded(UUID.randomUUID().toString(), label.trim()));
        }
        entity.setOptions(options);
        entity.setVotes(new ArrayList<>());

        DecisionEntity saved = decisionRepository.save(entity);
        return toDto(saved, null, false);
    }

    public DecisionDTO getById(String id, String deviceId) {
        DecisionEntity entity = decisionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Decision not found"));
        closeIfPastDeadline(entity);
        boolean hasVoted = deviceId != null && hasDeviceVoted(entity, deviceId);
        return toDto(entity, deviceId, hasVoted);
    }

    public DecisionDTO vote(String id, VoteRequest voteRequest, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Missing X-Device-Id header");
        }

        DecisionEntity entity = decisionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Decision not found"));

        closeIfPastDeadline(entity);
        if (entity.getStatus() == DecisionStatus.CLOSED) {
            throw new ApiException(HttpStatus.GONE, "This decision is closed");
        }

        if (hasDeviceVoted(entity, deviceId)) {
            throw new ApiException(HttpStatus.CONFLICT, "This device has already voted");
        }

        String optionId = voteRequest.getOptionId();
        boolean validOption = entity.getOptions().stream().anyMatch(o -> Objects.equals(o.getId(), optionId));
        if (!validOption) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid option");
        }

        entity.getVotes().add(new VoteRecord(optionId, deviceId, Instant.now()));
        decisionRepository.save(entity);

        closeIfPastDeadline(entity);
        return toDto(entity, deviceId, true);
    }

    private void closeIfPastDeadline(DecisionEntity entity) {
        if (entity.getStatus() == DecisionStatus.OPEN
                && entity.getClosesAt() != null
                && Instant.now().isAfter(entity.getClosesAt())) {
            entity.setStatus(DecisionStatus.CLOSED);
            decisionRepository.save(entity);
        }
    }

    private boolean hasDeviceVoted(DecisionEntity entity, String deviceId) {
        return entity.getVotes().stream().anyMatch(v -> Objects.equals(v.getDeviceId(), deviceId));
    }

    private DecisionDTO toDto(DecisionEntity entity, String deviceId, boolean hasVotedKnown) {
        boolean hasVoted = hasVotedKnown
                || (deviceId != null && hasDeviceVoted(entity, deviceId));

        Map<String, Long> countsByOption = entity.getVotes().stream()
                .collect(Collectors.groupingBy(VoteRecord::getOptionId, Collectors.counting()));

        List<OptionViewDTO> optionDtos = entity.getOptions().stream()
                .map(o -> new OptionViewDTO(
                        o.getId(),
                        o.getLabel(),
                        countsByOption.getOrDefault(o.getId(), 0L).intValue()
                ))
                .toList();

        DecisionDTO dto = new DecisionDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setOptions(optionDtos);
        dto.setStatus(entity.getStatus());
        dto.setClosesAt(entity.getClosesAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setHasVoted(hasVoted);

        if (entity.getStatus() == DecisionStatus.OPEN) {
            dto.setVoteActivity(buildVoteActivity(entity));
        }

        if (entity.getStatus() == DecisionStatus.CLOSED) {
            dto.setResult(computeResult(entity).orElse(null));
        }

        return dto;
    }

    private static final int MAX_ACTIVITY = 50;

    private List<VoteActivityDTO> buildVoteActivity(DecisionEntity entity) {
        List<VoteRecord> votes = entity.getVotes();
        if (votes == null || votes.isEmpty()) {
            return List.of();
        }
        return votes.stream()
                .sorted(Comparator.comparing(VoteRecord::getVotedAt).reversed())
                .limit(MAX_ACTIVITY)
                .map(v -> {
                    String label = entity.getOptions().stream()
                            .filter(o -> Objects.equals(o.getId(), v.getOptionId()))
                            .map(OptionEmbedded::getLabel)
                            .findFirst()
                            .orElse("?");
                    VoteActivityDTO row = new VoteActivityDTO();
                    row.setOptionLabel(label);
                    row.setVotedAt(v.getVotedAt());
                    return row;
                })
                .toList();
    }

    /**
     * Highest vote count wins; ties broken by earliest first supporting vote among tied leaders.
     */
    Optional<DecisionResultDTO> computeResult(DecisionEntity entity) {
        List<VoteRecord> votes = entity.getVotes();
        if (votes == null || votes.isEmpty()) {
            return Optional.of(new DecisionResultDTO(null, null, "No votes were cast. No decision."));
        }

        Map<String, Long> counts = votes.stream()
                .collect(Collectors.groupingBy(VoteRecord::getOptionId, Collectors.counting()));

        long max = Collections.max(counts.values());
        List<String> topIds = counts.entrySet().stream()
                .filter(e -> e.getValue() == max)
                .map(Map.Entry::getKey)
                .toList();

        String winnerId;
        boolean tieBroken = false;
        if (topIds.size() == 1) {
            winnerId = topIds.get(0);
        } else {
            tieBroken = true;
            Map<String, Instant> firstVoteAt = new HashMap<>();
            for (String oid : topIds) {
                Instant first = votes.stream()
                        .filter(v -> Objects.equals(v.getOptionId(), oid))
                        .map(VoteRecord::getVotedAt)
                        .min(Comparator.naturalOrder())
                        .orElse(Instant.MAX);
                firstVoteAt.put(oid, first);
            }
            winnerId = topIds.stream()
                    .min(Comparator.comparing(firstVoteAt::get))
                    .orElse(topIds.get(0));
        }

        String winnerLabel = entity.getOptions().stream()
                .filter(o -> Objects.equals(o.getId(), winnerId))
                .map(OptionEmbedded::getLabel)
                .findFirst()
                .orElse("?");

        long winnerVotes = counts.getOrDefault(winnerId, 0L);
        String explanation = tieBroken
                ? winnerLabel + " wins with " + winnerVotes + " votes. Tie broken by earliest supporting vote."
                : winnerLabel + " wins with " + winnerVotes + " vote" + (winnerVotes == 1 ? "" : "s") + ".";

        return Optional.of(new DecisionResultDTO(winnerId, winnerLabel, explanation));
    }
}
