package com.smerteliko.knowledgehub.controller;

import com.smerteliko.knowledgehub.entity.Tag;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Map<String, String> request, @AuthenticationPrincipal User user) {
        String name = request.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Tag newTag = tagService.createTag(name, user);
        return ResponseEntity.ok(newTag);
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getTags(@AuthenticationPrincipal User user) {
        List<Tag> tags = tagService.findAllByUserId(user.getId());
        return ResponseEntity.ok(tags);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        tagService.deleteTagById(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
