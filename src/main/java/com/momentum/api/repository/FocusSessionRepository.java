package com.momentum.api.repository;

import com.momentum.api.entity.FocusSession;
import com.momentum.api.entity.FocusSessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FocusSessionRepository extends JpaRepository<FocusSession, UUID> {

    Optional<FocusSession> findByIdAndUserId(UUID id, UUID userId);

    List<FocusSession> findByUserIdOrderByStartedAtDesc(UUID userId);

    Page<FocusSession> findByUserIdOrderByStartedAtDesc(UUID userId, Pageable pageable);

    Page<FocusSession> findByUserIdAndTagIdOrderByStartedAtDesc(UUID userId, UUID tagId, Pageable pageable);

    @Query("select f from FocusSession f where f.userId = :userId " +
           "and f.startedAt >= :from and f.startedAt < :to order by f.startedAt asc")
    List<FocusSession> findByUserIdAndStartedAtBetween(
            @Param("userId") UUID userId,
            @Param("from") Instant from,
            @Param("to") Instant to);

    Optional<FocusSession> findFirstByUserIdAndStatusOrderByStartedAtDesc(
            UUID userId, FocusSessionStatus status);

    @Query("select coalesce(sum(f.actualDurationSeconds), 0) from FocusSession f " +
           "where f.taskId = :taskId and f.status = 'COMPLETED' and f.type = 'FOCUS'")
    long sumFocusSecondsByTaskId(@Param("taskId") UUID taskId);
}
