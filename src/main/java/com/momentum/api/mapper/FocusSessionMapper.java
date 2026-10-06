package com.momentum.api.mapper;

import com.momentum.api.dto.focus.FocusSessionResponse;
import com.momentum.api.entity.FocusSession;
import com.momentum.api.entity.FocusSessionStatus;

import java.time.Instant;

public final class FocusSessionMapper {

    private FocusSessionMapper() {}
    
    public static FocusSessionResponse toResponse(FocusSession session) {
        int remaining = computeRemainingSeconds(session);

        return new FocusSessionResponse(
                session.getId(), session.getTaskId(), session.getTagId(), session.getType(), session.getStatus(),
                session.getPlannedDurationSeconds(), session.getStartedAt(), session.getPausedAt(),
                session.getAccumulatedPauseSeconds(), session.getEndedAt(),
                session.getActualDurationSeconds(), remaining);
    }

    public static int computeRemainingSeconds(FocusSession session) {
        if (session.getStatus() == FocusSessionStatus.COMPLETED
                || session.getStatus() == FocusSessionStatus.CANCELLED) {
            return 0;
        }

        Instant reference = session.getStatus() == FocusSessionStatus.PAUSED
                ? session.getPausedAt()
                : Instant.now();

        long elapsedTotal = reference.getEpochSecond() - session.getStartedAt().getEpochSecond();
        long effectiveElapsed = elapsedTotal - session.getAccumulatedPauseSeconds();
        long remaining = session.getPlannedDurationSeconds() - effectiveElapsed;

        return (int) Math.max(remaining, 0);
    }
}
