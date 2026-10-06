package com.momentum.api.dto.tag;

import com.momentum.api.entity.TagKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequest(
        @NotBlank @Size(max = 60) String name,
        String color,
        TagKind kind
) {}
