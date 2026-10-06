package com.momentum.api.service;

import com.momentum.api.dto.statistics.StatisticsResponse;
import com.momentum.api.dto.tag.TagWeeklyStatResponse;
import com.momentum.api.entity.FocusSession;
import com.momentum.api.entity.FocusSessionStatus;
import com.momentum.api.entity.FocusSessionType;
import com.momentum.api.entity.Tag;
import com.momentum.api.entity.TaskStatus;
import com.momentum.api.repository.FocusSessionRepository;
import com.momentum.api.repository.TagRepository;
import com.momentum.api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final FocusSessionRepository focusSessionRepository;
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;

    private static final ZoneOffset ZONE = ZoneOffset.UTC;

    public List<TagWeeklyStatResponse> getWeeklyTagBreakdown(UUID userId) {
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        Map<UUID, Tag> tagsById = new HashMap<>();
        tagRepository.findByUserIdOrderByNameAsc(userId).forEach(t -> tagsById.put(t.getId(), t));

        Map<UUID, long[]> totals = new LinkedHashMap<>(); // tagId -> [seconds, count]

        for (FocusSession s : focusSessionRepository.findByUserIdOrderByStartedAtDesc(userId)) {
            if (s.getTagId() == null) continue;
            if (s.getStatus() != FocusSessionStatus.COMPLETED || s.getType() != FocusSessionType.FOCUS) continue;
            if (s.getStartedAt().isBefore(sevenDaysAgo)) continue;

            long[] acc = totals.computeIfAbsent(s.getTagId(), k -> new long[2]);
            acc[0] += s.getActualDurationSeconds() != null ? s.getActualDurationSeconds() : 0;
            acc[1] += 1;
        }

        List<TagWeeklyStatResponse> result = new ArrayList<>();
        for (var entry : totals.entrySet()) {
            Tag tag = tagsById.get(entry.getKey());
            if (tag == null) continue;
            result.add(new TagWeeklyStatResponse(
                    tag.getId(), tag.getName(), tag.getColor(), tag.getKind(),
                    entry.getValue()[0], entry.getValue()[1]));
        }
        result.sort((a, b) -> Long.compare(b.totalSeconds(), a.totalSeconds()));
        return result;
    }

    public StatisticsResponse getStatistics(UUID userId) {
        List<FocusSession> sessions = focusSessionRepository.findByUserIdOrderByStartedAtDesc(userId);

        List<FocusSession> completedFocus = sessions.stream()
                .filter(s -> s.getStatus() == FocusSessionStatus.COMPLETED)
                .filter(s -> s.getType() == FocusSessionType.FOCUS)
                .toList();

        long totalFocusSeconds = completedFocus.stream()
                .mapToLong(s -> s.getActualDurationSeconds() != null ? s.getActualDurationSeconds() : 0)
                .sum();

        long completedSessions = sessions.stream()
                .filter(s -> s.getStatus() == FocusSessionStatus.COMPLETED).count();

        long completedTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);

        Set<java.time.LocalDate> activeDays = new TreeSet<>();
        for (FocusSession s : completedFocus) {
            activeDays.add(s.getStartedAt().atZone(ZONE).toLocalDate());
        }

        double averageDailyMinutes = activeDays.isEmpty() ? 0
                : (totalFocusSeconds / 60.0) / activeDays.size();

        int[] streaks = computeStreaks(activeDays);

        // Foco por dia da semana, ultimos 7 dias corridos
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        Map<String, Long> byWeekday = new LinkedHashMap<>();
        for (DayOfWeek d : DayOfWeek.values()) byWeekday.put(d.name(), 0L);

        for (FocusSession s : completedFocus) {
            if (s.getStartedAt().isBefore(sevenDaysAgo)) continue;
            DayOfWeek day = s.getStartedAt().atZone(ZONE).getDayOfWeek();
            long dur = s.getActualDurationSeconds() != null ? s.getActualDurationSeconds() : 0;
            byWeekday.merge(day.name(), dur, Long::sum);
        }

        return new StatisticsResponse(
                totalFocusSeconds,
                completedFocus.size(),
                completedSessions,
                completedTasks,
                Math.round(averageDailyMinutes * 10) / 10.0,
                streaks[0],
                streaks[1],
                byWeekday);
    }

    private int[] computeStreaks(Set<java.time.LocalDate> activeDays) {
        if (activeDays.isEmpty()) return new int[]{0, 0};

        List<java.time.LocalDate> sorted = new ArrayList<>(activeDays);
        int longest = 1, current = 1;

        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).minusDays(1).equals(sorted.get(i - 1))) {
                current++;
            } else {
                current = 1;
            }
            longest = Math.max(longest, current);
        }

        java.time.LocalDate today = Instant.now().atZone(ZONE).toLocalDate();
        java.time.LocalDate lastActive = sorted.get(sorted.size() - 1);
        int currentStreak = (lastActive.equals(today) || lastActive.equals(today.minusDays(1))) ? current : 0;

        return new int[]{currentStreak, longest};
    }
}
