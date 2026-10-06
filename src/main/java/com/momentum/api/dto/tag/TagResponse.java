package com.momentum.api.dto.tag;

import com.momentum.api.entity.TagKind;

import java.util.UUID;

public record TagResponse(UUID id, String name, String color, TagKind kind) {}
