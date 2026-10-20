package com.proj.mapper;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.proj.dto.CandidateRequest;
import com.proj.dto.CandidateResponse;
import com.proj.dto.StageHistoryResponse;
import com.proj.entity.Candidate;
import com.proj.entity.StageHistory;

@Component
public class CandidateMapper {

	public Candidate toEntity(CandidateRequest r) {
        Candidate c = new Candidate();
        apply(c, r);
        return c;
    }

    /** Copies request fields onto an existing entity. Does not touch stage, id, or timestamps. */
    public void apply(Candidate c, CandidateRequest r) {
        c.setName(r.name().trim());
        c.setEmail(r.email().trim().toLowerCase());
        c.setPhone(blankToNull(r.phone()));
        c.setRoleApplied(r.roleApplied().trim());
        c.setSkills(joinSkills(r.skills()));
        c.setExperienceYears(r.experienceYears());
        c.setSource(blankToNull(r.source()));
        c.setResumeUrl(blankToNull(r.resumeUrl()));
        c.setNotes(blankToNull(r.notes()));
    }

    public CandidateResponse toResponse(Candidate c) {
        return new CandidateResponse(
                c.getId(),
                c.getName(),
                c.getEmail(),
                c.getPhone(),
                c.getRoleApplied(),
                splitSkills(c.getSkills()),
                c.getExperienceYears(),
                c.getSource(),
                c.getResumeUrl(),
                c.getNotes(),
                c.getCurrentStage(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }

    public StageHistoryResponse toResponse(StageHistory h) {
        return new StageHistoryResponse(
                h.getId(),
                h.getFromStage(),
                h.getToStage(),
                h.getChangedAt(),
                h.getNote()
        );
    }

    // ---- helpers ----+95

    private static String joinSkills(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return null;
        }
        Set<String> cleaned = skills.stream()
                .map(s -> s.replace(",", " ").trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return cleaned.isEmpty() ? null : String.join(",", cleaned);
    }

    private static List<String> splitSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return List.of();
        }
        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
	
}
