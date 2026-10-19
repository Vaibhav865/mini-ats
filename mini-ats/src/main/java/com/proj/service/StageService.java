package com.proj.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj.dto.CandidateResponse;
import com.proj.dto.StageChangeRequest;
import com.proj.dto.StageHistoryResponse;
import com.proj.exception.InvalidStageTransitionException;
import com.proj.exception.ResourceNotFoundException;
import com.proj.mapper.CandidateMapper;
import com.proj.miniats.entity.Candidate;
import com.proj.miniats.entity.StageHistory;
import com.proj.miniats.enums.Stage;
import com.proj.repository.CandidateRepository;
import com.proj.repository.StageHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StageService {
			
		
	/**
     * Allowed moves. Forward one step, back one step (so a mis-drag on the board can be undone),
     * rejection from any active stage, and reopening a rejected candidate back to APPLIED.
     * HIRED is final.
     */
    private static final Map<Stage, Set<Stage>> ALLOWED = new EnumMap<>(Stage.class);

    static {
        ALLOWED.put(Stage.APPLIED, Set.of(Stage.SCREENING, Stage.REJECTED));
        ALLOWED.put(Stage.SCREENING, Set.of(Stage.APPLIED, Stage.INTERVIEW, Stage.REJECTED));
        ALLOWED.put(Stage.INTERVIEW, Set.of(Stage.SCREENING, Stage.OFFER, Stage.REJECTED));
        ALLOWED.put(Stage.OFFER, Set.of(Stage.INTERVIEW, Stage.HIRED, Stage.REJECTED));
        ALLOWED.put(Stage.HIRED, Set.of());
        ALLOWED.put(Stage.REJECTED, Set.of(Stage.APPLIED));
    }

    private final CandidateRepository candidateRepository;
    private final StageHistoryRepository historyRepository;
    private final CandidateMapper mapper;

    @Transactional
    public CandidateResponse changeStage(Long candidateId, StageChangeRequest request) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", candidateId));

        Stage from = candidate.getCurrentStage();
        Stage to = request.toStage();

        if (from == to) {
            throw new InvalidStageTransitionException("Candidate is already in stage " + from);
        }
        if (!ALLOWED.get(from).contains(to)) {
            throw new InvalidStageTransitionException(
                    "Cannot move candidate from " + from + " to " + to
                            + (ALLOWED.get(from).isEmpty()
                            ? " (" + from + " is a final stage)"
                            : ". Allowed next stages: " + ALLOWED.get(from)));
        }

        candidate.setCurrentStage(to);
        candidateRepository.save(candidate);

        historyRepository.save(StageHistory.builder()
                .candidate(candidate)
                .fromStage(from)
                .toStage(to)
                .note(request.note() == null || request.note().isBlank() ? null : request.note().trim())
                .build());

        return mapper.toResponse(candidate);
    }

    @Transactional(readOnly = true)
    public List<StageHistoryResponse> getHistory(Long candidateId) {
        if (!candidateRepository.existsById(candidateId)) {
            throw new ResourceNotFoundException("Candidate", candidateId);
        }
        return historyRepository.findByCandidateIdOrderByChangedAtAsc(candidateId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
	
}
