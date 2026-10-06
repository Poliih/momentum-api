package com.momentum.api.service;

import com.momentum.api.dto.task.TaskItemRequest;
import com.momentum.api.dto.task.TaskRequest;
import com.momentum.api.entity.Task;
import com.momentum.api.entity.TaskItem;
import com.momentum.api.entity.TaskPriority;
import com.momentum.api.entity.TaskStatus;
import com.momentum.api.exception.ApiException;
import com.momentum.api.repository.FocusSessionRepository;
import com.momentum.api.repository.TaskItemRepository;
import com.momentum.api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskItemRepository taskItemRepository;
    private final FocusSessionRepository focusSessionRepository;

    public List<Task> listForUser(UUID userId, TaskStatus status) {
        return status == null
                ? taskRepository.findByUserIdOrderByCreatedAtDesc(userId)
                : taskRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
    }

    public Task get(UUID id, UUID userId) {
        return taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ApiException.notFound("Tarefa nao encontrada"));
    }

    public long focusedSecondsFor(UUID taskId) {
        return focusSessionRepository.sumFocusSecondsByTaskId(taskId);
    }

    public Task create(UUID userId, TaskRequest request) {
        Task task = Task.builder()
                .userId(userId)
                .title(request.title())
                .description(request.description())
                .priority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM)
                .estimatedPomodoros(request.estimatedPomodoros() != null ? request.estimatedPomodoros() : 1)
                .goalMinutes(request.goalMinutes())
                .build();
        return taskRepository.save(task);
    }

    public Task update(UUID id, UUID userId, TaskRequest request) {
        Task task = get(id, userId);
        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.priority() != null) task.setPriority(request.priority());
        if (request.estimatedPomodoros() != null) task.setEstimatedPomodoros(request.estimatedPomodoros());
        task.setGoalMinutes(request.goalMinutes());
        return taskRepository.save(task);
    }

    public void delete(UUID id, UUID userId) {
        Task task = get(id, userId);
        taskRepository.delete(task);
    }

    public Task complete(UUID id, UUID userId) {
        Task task = get(id, userId);
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(Instant.now());
        return taskRepository.save(task);
    }

    public Task reopen(UUID id, UUID userId) {
        Task task = get(id, userId);
        task.setStatus(TaskStatus.TODO);
        task.setCompletedAt(null);
        return taskRepository.save(task);
    }

    public Task changePriority(UUID id, UUID userId, TaskPriority priority) {
        Task task = get(id, userId);
        task.setPriority(priority);
        return taskRepository.save(task);
    }

    public Task addItem(UUID taskId, UUID userId, TaskItemRequest request) {
        Task task = get(taskId, userId);
        int nextPosition = task.getItems().size();
        TaskItem item = TaskItem.builder()
                .taskId(task.getId())
                .title(request.title())
                .position(nextPosition)
                .build();
        taskItemRepository.save(item);
        return get(taskId, userId);
    }

    public Task toggleItem(UUID taskId, UUID itemId, UUID userId) {
        Task task = get(taskId, userId);
        TaskItem item = task.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> ApiException.notFound("Item da checklist nao encontrado"));

        item.setCompleted(!item.getCompleted());
        item.setCompletedAt(item.getCompleted() ? Instant.now() : null);
        taskItemRepository.save(item);
        return get(taskId, userId);
    }

    public void removeItem(UUID taskId, UUID itemId, UUID userId) {
        Task task = get(taskId, userId);
        boolean belongs = task.getItems().stream().anyMatch(i -> i.getId().equals(itemId));
        if (!belongs) throw ApiException.notFound("Item da checklist nao encontrado");
        taskItemRepository.deleteById(itemId);
    }

    public void incrementCompletedPomodoros(UUID taskId) {
        taskRepository.findById(taskId).ifPresent(task -> {
            task.setCompletedPomodoros(task.getCompletedPomodoros() + 1);
            if (task.getStatus() == TaskStatus.TODO) {
                task.setStatus(TaskStatus.IN_PROGRESS);
            }
            taskRepository.save(task);
        });
    }
}
