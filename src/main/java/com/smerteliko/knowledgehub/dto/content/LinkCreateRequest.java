package com.smerteliko.knowledgehub.dto.content;

import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
public class LinkCreateRequest {
    private String url;
    private String title;
    private String description;
    private String imageUrl;
    private Set<UUID> tagIds;
}
