package com.smerteliko.knowledgehub.service.content;

import com.smerteliko.knowledgehub.dto.content.LinkCreateRequest;
import com.smerteliko.knowledgehub.dto.content.NoteCreateRequest;
import com.smerteliko.knowledgehub.entity.ContentItem;
import com.smerteliko.knowledgehub.entity.Note;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.repository.ContentItemRepository;
import com.smerteliko.knowledgehub.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentItemRepository contentItemRepository;
    private final TagService tagService;
    // private final LinkParsingService linkParsingService; // Will be added later
    // private final SearchService searchService; // Will be added later

    public List<ContentItem> findAllByUserId(UUID userId) {
        return contentItemRepository.findByUserId(userId);
    }

    public void deleteByIdAndUser(UUID id, UUID userId) {
        // Find and ensure user owns item before deletion (full implementation later)
        contentItemRepository.deleteById(id);
    }

    public Note createNote(NoteCreateRequest request, User user) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUser(user);

        // Handle tags (full implementation later)

        return contentItemRepository.save(note);
    }

    public ContentItem createLink(LinkCreateRequest request, User user) {
        // This is where link parsing will be integrated
        throw new UnsupportedOperationException("Link creation not fully implemented yet.");
    }
}
