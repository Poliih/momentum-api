package com.momentum.api.controller;

import com.momentum.api.dto.tag.TagRequest;
import com.momentum.api.dto.tag.TagResponse;
import com.momentum.api.entity.Tag;
import com.momentum.api.security.CurrentUser;
import com.momentum.api.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    private TagResponse toResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName(), tag.getColor(), tag.getKind());
    }

    @GetMapping
    public List<TagResponse> list() {
        return tagService.listForUser(CurrentUser.id()).stream().map(this::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<TagResponse> create(@Valid @RequestBody TagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(tagService.create(CurrentUser.id(), request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tagService.delete(id, CurrentUser.id());
        return ResponseEntity.noContent().build();
    }
}
