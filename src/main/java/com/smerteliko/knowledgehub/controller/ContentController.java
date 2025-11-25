package com.smerteliko.knowledgehub.controller;

import com.smerteliko.knowledgehub.dto.content.LinkCreateRequest;
import com.smerteliko.knowledgehub.dto.content.NoteCreateRequest;
import com.smerteliko.knowledgehub.entity.ContentItem;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.service.content.ContentService;
import com.smerteliko.knowledgehub.service.export.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final ExportService exportService;

    @GetMapping
    public ResponseEntity<List<ContentItem>> listContent(@AuthenticationPrincipal User user) {
        List<ContentItem> items = contentService.findAllByUserId(user.getId());
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        contentService.deleteByIdAndUser(id, user.getId());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/note")
    public ResponseEntity<ContentItem> createNote(@RequestBody NoteCreateRequest request, @AuthenticationPrincipal User user) {

        if (request.getTitle() == null || request.getTitle().isBlank() || request.getContent() == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(contentService.createNote(request, user));
    }

    @PostMapping("/link")
    public ResponseEntity<ContentItem> createLink(@RequestBody LinkCreateRequest request, @AuthenticationPrincipal User user) {
        if (request.getUrl() == null || request.getUrl().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(contentService.createLink(request, user));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<?> exportContent(@PathVariable UUID id,
                                           @RequestParam String format,
                                           @AuthenticationPrincipal User user) throws IOException {

        ContentItem item = contentService.findByIdAndUser(id, user.getId());

        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        String filename = item.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");

        switch (format.toLowerCase()) {
            case "pdf":
                byte[] pdfBytes = exportService.exportToPdf(item);
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

            case "md":
                String markdown = exportService.exportToMarkdown(item);
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".md\"")
                    .contentType(MediaType.parseMediaType("text/markdown"))
                    .body(markdown);

            default:
                return ResponseEntity.badRequest().body("Unsupported format. Use 'pdf' or 'md'.");
        }
    }
}