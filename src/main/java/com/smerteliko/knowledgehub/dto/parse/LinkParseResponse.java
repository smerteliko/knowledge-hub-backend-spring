package com.smerteliko.knowledgehub.dto.parse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkParseResponse {
    private String title;
    private String description;
    private String imageUrl;
    private String faviconUrl;
    private String sourceUrl;
}
