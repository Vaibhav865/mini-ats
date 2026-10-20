package com.proj.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.proj.entity.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate,Long>, JpaSpecificationExecutor<Candidate>  {

		boolean existsByEmailIgnoreCase(String email);

	    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
		
	    @Query("select distinct c.roleApplied from Candidate c order by c.roleApplied")
	    List<String> findDistinctRoles();
}
