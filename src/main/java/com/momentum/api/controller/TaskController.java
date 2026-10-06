package com.momentum.api.controller;

import com.momentum.api.dto.task.TaskItemRequest;
import com.momentum.api.dto.task.TaskRequest;
import com.momentum.api.dto.task.TaskResponse;
import com.momentum.api.entity.Task;
import com.momentum.api.entity.TaskPriority;
import com.momentum.api.entity.TaskStatus;
import com.momentum.api.mapper.TaskMapper;
import com.momentum.api.security.CurrentUser;
import com.momentum.api.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private TaskResponse toResponse(Task task) {
        return TaskMapper.toResponse(task, taskService.focusedSecondsFor(task.getId()));
    }

    @GetMapping
    public List<TaskResponse> list(@RequestParam(required = false) TaskStatus status) {
        return taskService.listForUser(CurrentUser.id(), status).stream()
                .map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable UUID id) {
        return toResponse(taskService.get(id, CurrentUser.id()));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        var task = taskService.create(CurrentUser.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(task));
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody TaskRequest request) {
        return toResponse(taskService.update(id, CurrentUser.id(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id, CurrentUser.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public TaskResponse complete(@PathVariable UUID id) {
        return toResponse(taskService.complete(id, CurrentUser.id()));
    }

    @PostMapping("/{id}/reopen")
    public TaskResponse reopen(@PathVariable UUID id) {
        return toResponse(taskService.reopen(id, CurrentUser.id()));
    }

    @PatchMapping("/{id}/priority")
    public TaskResponse changePriority(@PathVariable UUID id, @RequestParam TaskPriority priority) {
        return toResponse(taskService.changePriority(id, CurrentUser.id(), priority));
    }

    @PostMapping("/{id}/items")
    public TaskResponse addItem(@PathVariable UUID id, @Valid @RequestBody TaskItemRequest request) {
        return toResponse(taskService.addItem(id, CurrentUser.id(), request));
    }

    @PatchMapping("/{id}/items/{itemId}/toggle")
    public TaskResponse toggleItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        return toResponse(taskService.toggleItem(id, itemId, CurrentUser.id()));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        taskService.removeItem(id, itemId, CurrentUser.id());
        return ResponseEntity.noContent().build();
    }
}
