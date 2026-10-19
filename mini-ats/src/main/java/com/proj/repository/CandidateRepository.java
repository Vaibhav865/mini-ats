package com.proj.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.miniats.entity.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate,Long> {

	 boolean existsByEmailIgnoreCase(String email);

	    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
		
}
