package com.momentum.api.repository;

import com.momentum.api.entity.PomodoroSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PomodoroSettingsRepository extends JpaRepository<PomodoroSettings, UUID> {
    Optional<PomodoroSettings> findByUserId(UUID userId);
}
