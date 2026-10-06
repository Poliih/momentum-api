package com.momentum.api.dto.tag;

import com.momentum.api.entity.TagKind;

import java.util.UUID;

public record TagWeeklyStatResponse(
        UUID tagId,
        String tagName,
        String color,
        TagKind kind,
        long totalSeconds,
        long sessionCount
) {}
