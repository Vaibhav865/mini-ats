package com.proj.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj.dto.CandidateRequest;
import com.proj.dto.CandidateResponse;
import com.proj.dto.CandidateSearchCriteria;
import com.proj.dto.PageResponse;
import com.proj.entity.Candidate;
import com.proj.entity.StageHistory;
import com.proj.enums.Stage;
import com.proj.exception.DuplicateEmailException;
import com.proj.exception.ResourceNotFoundException;
import com.proj.mapper.CandidateMapper;
import com.proj.repository.CandidateRepository;
import com.proj.repository.CandidateSpecifications;
import com.proj.repository.StageHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("createdAt", "updatedAt", "name", "experienceYears");
    private static final int MAX_PAGE_SIZE = 100;

    private final CandidateRepository candidateRepository;
    private final StageHistoryRepository historyRepository;
    private final CandidateMapper mapper;

    @Transactional
    public CandidateResponse create(CandidateRequest request) {
        String email = request.email().trim().toLowerCase();
        if (candidateRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }

        Candidate candidate = mapper.toEntity(request);
        candidate.setCurrentStage(Stage.APPLIED);
        Candidate saved = candidateRepository.save(candidate);

        historyRepository.save(StageHistory.builder()
                .candidate(saved)
                .fromStage(null)
                .toStage(Stage.APPLIED)
                .note("Candidate added")
                .build());

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CandidateResponse getById(Long id) {
        return mapper.toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<CandidateResponse> search(CandidateSearchCriteria criteria,
                                                  int page, int size,
                                                  String sortBy, String direction) {
        if (!SORTABLE_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Cannot sort by '" + sortBy + "'. Allowed fields: " + SORTABLE_FIELDS);
        }

        Sort.Direction dir;
        if ("asc".equalsIgnoreCase(direction)) {
            dir = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(direction)) {
            dir = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("Direction must be 'asc' or 'desc'");
        }

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        // Secondary sort on id keeps page boundaries stable when many rows tie on the main field
        Sort sort = Sort.by(dir, sortBy).and(Sort.by(Sort.Direction.DESC, "id"));
        Specification<Candidate> spec = CandidateSpecifications.from(criteria);
        Pageable pageable = PageRequest.of(safePage, safeSize, sort);
        Page<Candidate> result = candidateRepository.findAll(spec, pageable);
        return PageResponse.from(result.map(mapper::toResponse));
//        return PageResponse.from(
//                candidateRepository
//                        .findAll(CandidateSpecifications.from(criteria), PageRequest.of(safePage, safeSize, sort))
//                        .map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public List<String> roles() {
        return candidateRepository.findDistinctRoles();
    }

    @Transactional
    public CandidateResponse update(Long id, CandidateRequest request) {
        Candidate candidate = findOrThrow(id);

        String email = request.email().trim().toLowerCase();
        if (candidateRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateEmailException(email);
        }

        mapper.apply(candidate, request);
        return mapper.toResponse(candidateRepository.save(candidate));
    }

    @Transactional
    public void delete(Long id) {
        Candidate candidate = findOrThrow(id);
        candidateRepository.delete(candidate);
    }

    Candidate findOrThrow(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", id));
    }
}