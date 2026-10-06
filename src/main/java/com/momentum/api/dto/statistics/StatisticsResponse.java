package com.momentum.api.dto.statistics;

import java.util.Map;

public record StatisticsResponse(
        long totalFocusSeconds,
        long totalPomodoros,
        long completedSessions,
        long completedTasks,
        double averageDailyMinutes,
        int currentStreak,
        int longestStreak,
        Map<String, Long> focusSecondsByWeekday 
) {}
