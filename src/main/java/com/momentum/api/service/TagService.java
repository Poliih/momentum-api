package com.momentum.api.service;

import com.momentum.api.dto.tag.TagRequest;
import com.momentum.api.entity.Tag;
import com.momentum.api.entity.TagKind;
import com.momentum.api.exception.ApiException;
import com.momentum.api.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> listForUser(UUID userId) {
        return tagRepository.findByUserIdOrderByNameAsc(userId);
    }

    public Tag create(UUID userId, TagRequest request) {
        if (tagRepository.existsByUserIdAndNameIgnoreCase(userId, request.name())) {
            throw ApiException.conflict("Voce ja tem uma tag com este nome");
        }
        Tag tag = Tag.builder()
                .userId(userId)
                .name(request.name())
                .color(request.color() != null ? request.color() : "#6c5ce7")
                .kind(request.kind() != null ? request.kind() : TagKind.NEUTRAL)
                .build();
        return tagRepository.save(tag);
    }

    public void delete(UUID id, UUID userId) {
        Tag tag = tagRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> ApiException.notFound("Tag nao encontrada"));
        tagRepository.delete(tag);
    }
}
