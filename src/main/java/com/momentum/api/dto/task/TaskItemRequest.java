package com.momentum.api.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskItemRequest(@NotBlank @Size(max = 200) String title) {}
