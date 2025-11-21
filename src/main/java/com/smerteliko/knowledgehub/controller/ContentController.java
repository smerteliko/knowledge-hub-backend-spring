package com.smerteliko.knowledgehub.controller;

import com.smerteliko.knowledgehub.dto.content.LinkCreateRequest;
import com.smerteliko.knowledgehub.dto.content.NoteCreateRequest;
import com.smerteliko.knowledgehub.entity.ContentItem;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.service.content.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    // --- GET, DELETE, LIST ---

    // GET /api/v1/content
    @GetMapping
    public ResponseEntity<List<ContentItem>> listContent(@AuthenticationPrincipal User user) {
        // Simplified for now, will add filtering/pagination later
        List<ContentItem> items = contentService.findAllByUserId(user.getId());
        return ResponseEntity.ok(items);
    }

    // DELETE /api/v1/content/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        contentService.deleteByIdAndUser(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // --- POST for Note/Link ---

    // POST /api/v1/content/note
    @PostMapping("/note")
    public ResponseEntity<ContentItem> createNote(@RequestBody NoteCreateRequest request,
                                                  @AuthenticationPrincipal User user) {
        // Implementation will follow
        return ResponseEntity.ok(contentService.createNote(request, user));
    }

    // POST /api/v1/content/link
    @PostMapping("/link")
    public ResponseEntity<ContentItem> createLink(@RequestBody LinkCreateRequest request,
                                                  @AuthenticationPrincipal User user) {
        // Implementation will follow
        return ResponseEntity.ok(contentService.createLink(request, user));
    }
}