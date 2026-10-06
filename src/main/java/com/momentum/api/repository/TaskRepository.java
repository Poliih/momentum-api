package com.momentum.api.repository;

import com.momentum.api.entity.Task;
import com.momentum.api.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Task> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, TaskStatus status);
    Optional<Task> findByIdAndUserId(UUID id, UUID userId);
    long countByUserIdAndStatus(UUID userId, TaskStatus status);
}
