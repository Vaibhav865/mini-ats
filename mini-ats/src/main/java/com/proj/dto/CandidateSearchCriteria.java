package com.proj.dto;

import java.time.LocalDate;

import com.proj.enums.Stage;

public record CandidateSearchCriteria(
		String q,
        Stage stage,
        String role,
        String source,
        LocalDate createdFrom,
        LocalDate createdTo
        ) {

}
