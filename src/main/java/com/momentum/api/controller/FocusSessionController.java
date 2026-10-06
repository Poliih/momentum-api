package com.momentum.api.controller;

import com.momentum.api.dto.common.PageResponse;
import com.momentum.api.dto.focus.FocusSessionResponse;
import com.momentum.api.dto.focus.StartFocusRequest;
import com.momentum.api.mapper.FocusSessionMapper;
import com.momentum.api.security.CurrentUser;
import com.momentum.api.service.FocusSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/focus")
@RequiredArgsConstructor
public class FocusSessionController {

    private final FocusSessionService focusSessionService;

    @PostMapping("/start")
    public ResponseEntity<FocusSessionResponse> start(@Valid @RequestBody StartFocusRequest request) {
        var session = focusSessionService.start(CurrentUser.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(FocusSessionMapper.toResponse(session));
    }

    @GetMapping("/{id}")
    public FocusSessionResponse get(@PathVariable UUID id) {
        return FocusSessionMapper.toResponse(focusSessionService.get(id, CurrentUser.id()));
    }

    @PostMapping("/{id}/pause")
    public FocusSessionResponse pause(@PathVariable UUID id) {
        return FocusSessionMapper.toResponse(focusSessionService.pause(id, CurrentUser.id()));
    }

    @PostMapping("/{id}/resume")
    public FocusSessionResponse resume(@PathVariable UUID id) {
        return FocusSessionMapper.toResponse(focusSessionService.resume(id, CurrentUser.id()));
    }

    @PostMapping("/{id}/complete")
    public FocusSessionResponse complete(@PathVariable UUID id) {
        return FocusSessionMapper.toResponse(focusSessionService.complete(id, CurrentUser.id()));
    }

    @PostMapping("/{id}/cancel")
    public FocusSessionResponse cancel(@PathVariable UUID id) {
        return FocusSessionMapper.toResponse(focusSessionService.cancel(id, CurrentUser.id()));
    }

    @GetMapping("/history")
    public List<FocusSessionResponse> history() {
        return focusSessionService.history(CurrentUser.id()).stream()
                .map(FocusSessionMapper::toResponse).toList();
    }

    @GetMapping("/history/page")
    public PageResponse<FocusSessionResponse> historyPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID tagId) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"));
        var result = focusSessionService.history(CurrentUser.id(), tagId, pageable);
        return PageResponse.from(result, FocusSessionMapper::toResponse);
    }
}
