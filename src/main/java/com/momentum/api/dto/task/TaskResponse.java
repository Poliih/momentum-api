package com.momentum.api.dto.task;

import com.momentum.api.entity.TaskPriority;
import com.momentum.api.entity.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        int estimatedPomodoros,
        int completedPomodoros,
        Integer goalMinutes,
        int focusedMinutes,
        Instant createdAt,
        Instant updatedAt,
        Instant completedAt,
        List<TaskItemResponse> items
) {}
