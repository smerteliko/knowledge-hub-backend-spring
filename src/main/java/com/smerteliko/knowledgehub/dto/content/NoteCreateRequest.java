package com.smerteliko.knowledgehub.dto.content;

import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
public class NoteCreateRequest {
    private String title;
    private String content;
    private Set<UUID> tagIds;
}
