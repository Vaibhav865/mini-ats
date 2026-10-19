package com.proj.controller;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.proj.dto.CandidateRequest;
import com.proj.dto.CandidateResponse;
import com.proj.dto.PageResponse;
import com.proj.service.CandidateService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {
		
    private final CandidateService candidateService;
    
    @PostMapping
    public ResponseEntity<CandidateResponse> create(@Valid @RequestBody CandidateRequest req){
        System.out.println("RECEIVED: " + req);

    	CandidateResponse created = candidateService.create(req);
    	
    	URI location = ServletUriComponentsBuilder.fromCurrentRequest()
    					.path("/{id}")
    					.buildAndExpand(created.id())
    					.toUri();   	
    	return ResponseEntity.created(location).body(created);	
    }
    
    
    @GetMapping("/{id}")
    public CandidateResponse getById(@PathVariable Long id) {
    	return candidateService.getById(id);
    }
	
    @GetMapping
    public PageResponse<CandidateResponse> list(
    		@PageableDefault(size = 10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable
    		){
    	return candidateService.list(pageable);
    }
    
    @PutMapping("/{id}")
    public CandidateResponse update(@PathVariable Long id, @Valid @RequestBody CandidateRequest request) {
        return candidateService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
    
    
}


















