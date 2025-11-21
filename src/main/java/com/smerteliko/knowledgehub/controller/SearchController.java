package com.smerteliko.knowledgehub.controller;

import com.smerteliko.knowledgehub.entity.ContentItem;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<ContentItem>> searchContent(@RequestParam String query, @AuthenticationPrincipal User user) {

        List<ContentItem> results = searchService.searchAllContent(query, user.getId());
        return ResponseEntity.ok(results);
    }
}
