package com.momentum.api.dto.task;

import java.time.Instant;
import java.util.UUID;

public record TaskItemResponse(
        UUID id,
        String title,
        boolean completed,
        int position,
        Instant completedAt
) {}
