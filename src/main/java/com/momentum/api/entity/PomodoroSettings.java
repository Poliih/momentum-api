package com.momentum.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pomodoro_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PomodoroSettings {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "focus_minutes", nullable = false)
    @Builder.Default
    private Integer focusMinutes = 25;

    @Column(name = "short_break_minutes", nullable = false)
    @Builder.Default
    private Integer shortBreakMinutes = 5;

    @Column(name = "long_break_minutes", nullable = false)
    @Builder.Default
    private Integer longBreakMinutes = 15;

    @Column(name = "sessions_before_long_break", nullable = false)
    @Builder.Default
    private Integer sessionsBeforeLongBreak = 4;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
