package com.momentum.api.mapper;

import com.momentum.api.dto.task.TaskItemResponse;
import com.momentum.api.dto.task.TaskResponse;
import com.momentum.api.entity.Task;
import com.momentum.api.entity.TaskItem;

public final class TaskMapper {

    private TaskMapper() {}

    public static TaskItemResponse toResponse(TaskItem item) {
        return new TaskItemResponse(
                item.getId(), item.getTitle(), item.getCompleted(),
                item.getPosition(), item.getCompletedAt());
    }

    public static TaskResponse toResponse(Task task, long focusedSeconds) {
        return new TaskResponse(
                task.getId(), task.getTitle(), task.getDescription(), task.getStatus(),
                task.getPriority(), task.getEstimatedPomodoros(), task.getCompletedPomodoros(),
                task.getGoalMinutes(), (int) (focusedSeconds / 60),
                task.getCreatedAt(), task.getUpdatedAt(), task.getCompletedAt(),
                task.getItems().stream().map(TaskMapper::toResponse).toList());
    }
}
