package com.momentum.api.dto.focus;

import com.momentum.api.entity.FocusSessionStatus;
import com.momentum.api.entity.FocusSessionType;

import java.time.Instant;
import java.util.UUID;

public record FocusSessionResponse(
        UUID id,
        UUID taskId,
        UUID tagId,
        FocusSessionType type,
        FocusSessionStatus status,
        int plannedDurationSeconds,
        Instant startedAt,
        Instant pausedAt,
        int accumulatedPauseSeconds,
        Instant endedAt,
        Integer actualDurationSeconds,
        int remainingSeconds
) {}
