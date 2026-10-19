package com.proj.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proj.dto.CandidateResponse;
import com.proj.dto.StageChangeRequest;
import com.proj.dto.StageHistoryResponse;
import com.proj.service.StageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class StageController {

	private final StageService stageService;

    @PatchMapping("/{id}/stage")
    public CandidateResponse changeStage(@PathVariable Long id,
                                         @Valid @RequestBody StageChangeRequest request) {
        return stageService.changeStage(id, request);
    }

    @GetMapping("/{id}/history")
    public List<StageHistoryResponse> history(@PathVariable Long id) {
        return stageService.getHistory(id);
    }
}
