package com.smerteliko.knowledgehub.controller;

import com.smerteliko.knowledgehub.entity.Tag;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.service.tag.TagService;
import com.smerteliko.knowledgehub.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    // POST /api/v1/tags
    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Map<String, String> request,
                                         @AuthenticationPrincipal User user) {
        String name = request.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Tag newTag = tagService.createTag(name, user);
        return ResponseEntity.ok(newTag);
    }

    // GET /api/v1/tags
    @GetMapping
    public ResponseEntity<List<Tag>> getTags(@AuthenticationPrincipal User user) {
        List<Tag> tags = tagService.findAllByUserId(user.getId());
        return ResponseEntity.ok(tags);
    }

    // Note: We need a utility class to properly extract the User from SecurityContext.
    // Let's assume the current @AuthenticationPrincipal User is working for now.
}
