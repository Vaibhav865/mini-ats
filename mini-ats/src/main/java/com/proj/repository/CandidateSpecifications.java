package com.proj.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.proj.dto.CandidateSearchCriteria;
import com.proj.entity.Candidate;

import jakarta.persistence.criteria.Predicate;

public class CandidateSpecifications {
	
	
	 private CandidateSpecifications() {
	    }

	    public static Specification<Candidate> from(CandidateSearchCriteria c) {
	        return (root, query, cb) -> {
	            List<Predicate> predicates = new ArrayList<>();

	            // Free-text search across name, email, skills, and role
	            if (hasText(c.q())) {
	                String pattern = "%" + escapeLike(c.q().trim().toLowerCase()) + "%";
	                predicates.add(cb.or(
	                        cb.like(cb.lower(root.<String>get("name")), pattern, '\\'),
	                        cb.like(cb.lower(root.<String>get("email")), pattern, '\\'),
	                        cb.like(cb.lower(root.<String>get("skills")), pattern, '\\'),
	                        cb.like(cb.lower(root.<String>get("roleApplied")), pattern, '\\')
	                ));
	            }

	            if (c.stage() != null) {
	                predicates.add(cb.equal(root.get("currentStage"), c.stage()));
	            }

	            if (hasText(c.role())) {
	                String pattern = "%" + escapeLike(c.role().trim().toLowerCase()) + "%";
	                predicates.add(cb.like(cb.lower(root.<String>get("roleApplied")), pattern, '\\'));
	            }

	            if (hasText(c.source())) {
	                predicates.add(cb.equal(cb.lower(root.<String>get("source")), c.source().trim().toLowerCase()));
	            }

	            // Date range on createdAt: "from" is inclusive, "to" includes the whole end day
	            if (c.createdFrom() != null) {
	                predicates.add(cb.greaterThanOrEqualTo(
	                        root.get("createdAt"), c.createdFrom().atStartOfDay()));
	            }
	            if (c.createdTo() != null) {
	                predicates.add(cb.lessThan(
	                        root.get("createdAt"), c.createdTo().plusDays(1).atStartOfDay()));
	            }

	            return cb.and(predicates.toArray(new Predicate[0]));
	        };
	    }

	    private static boolean hasText(String s) {
	        return s != null && !s.isBlank();
	    }

	    /** Escapes LIKE wildcards so a search for "100%" or "a_b" is treated literally. */
	    private static String escapeLike(String s) {
	        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	    }
		
	
}
