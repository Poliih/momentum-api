package com.momentum.api.service;

import com.momentum.api.dto.focus.StartFocusRequest;
import com.momentum.api.entity.FocusSession;
import com.momentum.api.entity.FocusSessionStatus;
import com.momentum.api.entity.FocusSessionType;
import com.momentum.api.exception.ApiException;
import com.momentum.api.repository.FocusSessionRepository;
import com.momentum.api.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;
    private final TaskService taskService;
    private final TagRepository tagRepository;

    public FocusSession start(UUID userId, StartFocusRequest request) {
        // Garante que nao existam duas sessoes rodando/pausadas ao mesmo tempo
        focusSessionRepository.findFirstByUserIdAndStatusOrderByStartedAtDesc(userId, FocusSessionStatus.RUNNING)
                .ifPresent(s -> { throw ApiException.conflict("Ja existe uma sessao em andamento"); });
        focusSessionRepository.findFirstByUserIdAndStatusOrderByStartedAtDesc(userId, FocusSessionStatus.PAUSED)
                .ifPresent(s -> { throw ApiException.conflict("Existe uma sessao pausada. Finalize ou continue antes de iniciar outra."); });

        if (request.tagId() != null) {
            tagRepository.findByIdAndUserId(request.tagId(), userId)
                    .orElseThrow(() -> ApiException.badRequest("Tag invalida"));
        }

        FocusSession session = FocusSession.builder()
                .userId(userId)
                .taskId(request.taskId())
                .tagId(request.tagId())
                .type(request.type())
                .status(FocusSessionStatus.RUNNING)
                .plannedDurationSeconds(request.plannedDurationSeconds())
                .startedAt(Instant.now())
                .build();

        return focusSessionRepository.save(session);
    }

    public FocusSession pause(UUID id, UUID userId) {
        FocusSession session = get(id, userId);
        if (session.getStatus() != FocusSessionStatus.RUNNING) {
            throw ApiException.badRequest("Somente sessoes em execucao podem ser pausadas");
        }
        session.setStatus(FocusSessionStatus.PAUSED);
        session.setPausedAt(Instant.now());
        return focusSessionRepository.save(session);
    }

    public FocusSession resume(UUID id, UUID userId) {
        FocusSession session = get(id, userId);
        if (session.getStatus() != FocusSessionStatus.PAUSED) {
            throw ApiException.badRequest("Somente sessoes pausadas podem ser continuadas");
        }

        long pauseDuration = Instant.now().getEpochSecond() - session.getPausedAt().getEpochSecond();
        session.setAccumulatedPauseSeconds((int) (session.getAccumulatedPauseSeconds() + pauseDuration));
        session.setPausedAt(null);
        session.setStatus(FocusSessionStatus.RUNNING);
        return focusSessionRepository.save(session);
    }

    public FocusSession complete(UUID id, UUID userId) {
        FocusSession session = get(id, userId);
        if (session.getStatus() == FocusSessionStatus.COMPLETED || session.getStatus() == FocusSessionStatus.CANCELLED) {
            throw ApiException.badRequest("Esta sessao ja foi finalizada");
        }

        Instant now = Instant.now();
        long elapsedTotal = now.getEpochSecond() - session.getStartedAt().getEpochSecond();
        long actualSeconds = elapsedTotal - session.getAccumulatedPauseSeconds();

        session.setStatus(FocusSessionStatus.COMPLETED);
        session.setEndedAt(now);
        session.setActualDurationSeconds((int) Math.max(actualSeconds, 0));
        FocusSession saved = focusSessionRepository.save(session);

        if (session.getType() == FocusSessionType.FOCUS && session.getTaskId() != null) {
            taskService.incrementCompletedPomodoros(session.getTaskId());
        }

        return saved;
    }

    public FocusSession cancel(UUID id, UUID userId) {
        FocusSession session = get(id, userId);
        session.setStatus(FocusSessionStatus.CANCELLED);
        session.setEndedAt(Instant.now());
        return focusSessionRepository.save(session);
    }

    public FocusSession get(UUID id, UUID userId) {
        return focusSessionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ApiException.notFound("Sessao de foco nao encontrada"));
    }

    public List<FocusSession> history(UUID userId) {
        return focusSessionRepository.findByUserIdOrderByStartedAtDesc(userId);
    }

    public Page<FocusSession> history(UUID userId, UUID tagId, Pageable pageable) {
        return tagId != null
                ? focusSessionRepository.findByUserIdAndTagIdOrderByStartedAtDesc(userId, tagId, pageable)
                : focusSessionRepository.findByUserIdOrderByStartedAtDesc(userId, pageable);
    }
}
