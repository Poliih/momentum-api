package com.momentum.api.dto.task;

import com.momentum.api.entity.TaskPriority;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank @Size(max = 200) String title,
        String description,
        TaskPriority priority,
        @Min(1) @Max(50) Integer estimatedPomodoros,
        @Min(1) Integer goalMinutes
) {}
