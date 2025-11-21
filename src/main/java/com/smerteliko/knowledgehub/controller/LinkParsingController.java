package com.smerteliko.knowledgehub.controller;

import com.smerteliko.knowledgehub.dto.parse.LinkParseResponse;
import com.smerteliko.knowledgehub.service.parse.LinkParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
public class LinkParsingController {

    private final LinkParsingService linkParsingService;

    // This endpoint is used by the frontend to get the preview before saving the Link Entity
    @PostMapping("/parse")
    public ResponseEntity<LinkParseResponse> parseLink(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        LinkParseResponse response = linkParsingService.parseUrl(url);
        return ResponseEntity.ok(response);
    }
}
