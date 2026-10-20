package com.proj.dto;

import java.time.LocalDateTime;

import com.proj.enums.Stage;

public record StageHistoryResponse(
		Long id,
        Stage fromStage,
        Stage toStage,
        LocalDateTime changedAt,
        String note) {

}
