package com.proj.dto;

import com.proj.miniats.enums.Stage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StageChangeRequest(
@NotNull(message = "Target stage is required")
Stage toStage,

@Size(max = 500, message = "Note must be at most 500 characters")
String note) {

}
