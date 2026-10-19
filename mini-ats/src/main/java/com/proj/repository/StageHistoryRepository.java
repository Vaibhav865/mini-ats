package com.proj.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.miniats.entity.StageHistory;

public interface StageHistoryRepository extends JpaRepository<StageHistory,Long> {
	
    List<StageHistory> findByCandidateIdOrderByChangedAtAsc(Long candidateId);

	
}
