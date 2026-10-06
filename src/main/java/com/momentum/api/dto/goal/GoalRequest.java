package com.momentum.api.dto.goal;

import jakarta.validation.constraints.Min;

public record GoalRequest(
        @Min(1) Integer dailyFocusMinutes,
        @Min(1) Integer dailyPomodoros,
        @Min(1) Integer dailyTasks
) {}
