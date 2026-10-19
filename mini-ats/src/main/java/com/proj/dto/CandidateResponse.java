package com.proj.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.proj.miniats.enums.Stage;

public record CandidateResponse(
		Long id,
        String name,
        String email,
        String phone,
        String roleApplied,
        List<String> skills,
        Double experienceYears,
        String source,
        String resumeUrl,
        String notes,
        Stage currentStage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}
