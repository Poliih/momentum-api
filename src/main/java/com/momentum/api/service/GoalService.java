package com.momentum.api.service;

import com.momentum.api.dto.goal.GoalRequest;
import com.momentum.api.dto.goal.GoalResponse;
import com.momentum.api.entity.FocusSession;
import com.momentum.api.entity.FocusSessionStatus;
import com.momentum.api.entity.FocusSessionType;
import com.momentum.api.entity.Goal;
import com.momentum.api.entity.TaskStatus;
import com.momentum.api.repository.FocusSessionRepository;
import com.momentum.api.repository.GoalRepository;
import com.momentum.api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final FocusSessionRepository focusSessionRepository;
    private final TaskRepository taskRepository;

    public GoalResponse getForUser(UUID userId) {
        Goal goal = goalRepository.findByUserId(userId)
                .orElseGet(() -> goalRepository.save(Goal.builder().userId(userId).build()));

        LocalDate today = Instant.now().atZone(ZoneOffset.UTC).toLocalDate();
        Instant startOfDay = today.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<FocusSession> todaySessions = focusSessionRepository
                .findByUserIdAndStartedAtBetween(userId, startOfDay, endOfDay);

        int todayFocusSeconds = todaySessions.stream()
                .filter(s -> s.getStatus() == FocusSessionStatus.COMPLETED && s.getType() == FocusSessionType.FOCUS)
                .mapToInt(s -> s.getActualDurationSeconds() != null ? s.getActualDurationSeconds() : 0)
                .sum();

        long todayPomodoros = todaySessions.stream()
                .filter(s -> s.getStatus() == FocusSessionStatus.COMPLETED && s.getType() == FocusSessionType.FOCUS)
                .count();

        long todayCompletedTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);

        return new GoalResponse(
                goal.getId(), goal.getDailyFocusMinutes(), goal.getDailyPomodoros(), goal.getDailyTasks(),
                todayFocusSeconds / 60, (int) todayPomodoros, (int) todayCompletedTasks);
    }

    public GoalResponse update(UUID userId, GoalRequest request) {
        Goal goal = goalRepository.findByUserId(userId)
                .orElseGet(() -> Goal.builder().userId(userId).build());

        if (request.dailyFocusMinutes() != null) goal.setDailyFocusMinutes(request.dailyFocusMinutes());
        if (request.dailyPomodoros() != null) goal.setDailyPomodoros(request.dailyPomodoros());
        if (request.dailyTasks() != null) goal.setDailyTasks(request.dailyTasks());

        goalRepository.save(goal);
        return getForUser(userId);
    }
}
