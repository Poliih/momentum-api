package com.momentum.api.dto.goal;

import java.util.UUID;

public record GoalResponse(
        UUID id,
        int dailyFocusMinutes,
        int dailyPomodoros,
        int dailyTasks,
        int todayFocusMinutes,
        int todayPomodoros,
        int todayCompletedTasks
) {}
