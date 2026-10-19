package com.proj.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj.dto.CandidateRequest;
import com.proj.dto.CandidateResponse;
import com.proj.dto.PageResponse;
import com.proj.exception.DuplicateEmailException;
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
public class CandidateService {
		
		private final CandidateRepository candidateRepository;
		private final StageHistoryRepository historyRepository;
	    private final CandidateMapper mapper;

	    @Transactional
	    public CandidateResponse create(CandidateRequest req) {
	    	String email = req.email().trim().toLowerCase();
	    	
	    	if(candidateRepository.existsByEmailIgnoreCase(email)) {
	    		throw new DuplicateEmailException(email);
	    	}
	    	
	    	Candidate candidate = mapper.toEntity(req);
	    	candidate.setCurrentStage(Stage.APPLIED);
	    	Candidate saved = candidateRepository.save(candidate);
	    	
	    	historyRepository.save(StageHistory.builder()
	    					 .candidate(saved)
	    					 .fromStage(null)
	    					 .toStage(Stage.APPLIED)
	    					 .note("Candidate added")
	    					 .build()
	    					);
	    	
	    	return mapper.toResponse(saved);
	    
	    }
	    
	    @Transactional(readOnly = true)
	    public CandidateResponse getById(Long id) {
	    	return mapper.toResponse(findOrThrow(id));
	    }
	    
	    @Transactional(readOnly = true)
	    public PageResponse<CandidateResponse> list(Pageable pageble){
	    	return PageResponse.from(candidateRepository.findAll(pageble).map(mapper::toResponse));
	    }
	    
	    @Transactional
	    public CandidateResponse update(Long id, CandidateRequest req) {
	    	
	    	Candidate candidate = findOrThrow(id);
	    	
	    	String email = req.email().trim().toLowerCase();
	    	
	    	if(candidateRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
	    		throw new DuplicateEmailException(email);
	    	}
	    	 
	    	mapper.apply(candidate, req);
	    	return mapper.toResponse(candidateRepository.save(candidate));
	    	
	    }
	    
	    
	    @Transactional
	    public void delete(Long id) {
	    	Candidate candidate = findOrThrow(id);
	    	candidateRepository.delete(candidate);
	    }
	    
	    Candidate findOrThrow(Long id) {
	    	return candidateRepository.findById(id)
	    							  .orElseThrow(() -> new ResourceNotFoundException("Candidate", id) );
	    }
		
	
}
