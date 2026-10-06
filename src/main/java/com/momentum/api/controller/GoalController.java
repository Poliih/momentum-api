package com.momentum.api.controller;

import com.momentum.api.dto.goal.GoalRequest;
import com.momentum.api.dto.goal.GoalResponse;
import com.momentum.api.security.CurrentUser;
import com.momentum.api.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public GoalResponse get() {
        return goalService.getForUser(CurrentUser.id());
    }

    @PostMapping
    public GoalResponse update(@Valid @RequestBody GoalRequest request) {
        return goalService.update(CurrentUser.id(), request);
    }
}
