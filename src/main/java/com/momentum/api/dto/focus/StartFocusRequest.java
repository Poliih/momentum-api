package com.momentum.api.dto.focus;

import com.momentum.api.entity.FocusSessionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartFocusRequest(
        UUID taskId,
        UUID tagId,
        @NotNull FocusSessionType type,
        @Min(60) Integer plannedDurationSeconds
) {}
