package com.smerteliko.knowledgehub.service.content;

import com.smerteliko.knowledgehub.dto.content.LinkCreateRequest;
import com.smerteliko.knowledgehub.dto.content.NoteCreateRequest;
import com.smerteliko.knowledgehub.dto.parse.LinkParseResponse;
import com.smerteliko.knowledgehub.entity.ContentItem;
import com.smerteliko.knowledgehub.entity.Link;
import com.smerteliko.knowledgehub.entity.Note;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.es.entity.LinkIndex;
import com.smerteliko.knowledgehub.es.entity.NoteIndex;
import com.smerteliko.knowledgehub.es.repository.LinkSearchRepository;
import com.smerteliko.knowledgehub.es.repository.NoteSearchRepository;
import com.smerteliko.knowledgehub.repository.ContentItemRepository;
import com.smerteliko.knowledgehub.service.parse.LinkParsingService;
import com.smerteliko.knowledgehub.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentItemRepository contentItemRepository;
    private final TagService tagService;
    private final LinkParsingService linkParsingService;

    private final NoteSearchRepository noteSearchRepository;
    private final LinkSearchRepository linkSearchRepository;

    public List<ContentItem> findAllByUserId(UUID userId) {
        return contentItemRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteByIdAndUser(UUID id, UUID userId) {
        ContentItem item = contentItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Content item not found."));

        if (!item.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: User does not own this item.");
        }

        contentItemRepository.delete(item);

        if (item instanceof Note) {
            noteSearchRepository.deleteById(item.getId());
        } else if (item instanceof Link) {
            linkSearchRepository.deleteById(item.getId());
        }
    }

    @Transactional
    public Note createNote(NoteCreateRequest request, User user) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUser(user);

        note.setTags(tagService.findTagsByIds(request.getTagIds()));

        Note savedNote = contentItemRepository.save(note);

        noteSearchRepository.save(NoteIndex.fromNote(savedNote));

        return savedNote;
    }

    @Transactional
    public Link createLink(LinkCreateRequest request, User user) {
        LinkParseResponse parsedData = linkParsingService.parseUrl(request.getUrl());

        Link link = new Link();
        link.setUrl(request.getUrl());
        link.setUser(user);

        link.setTitle(request.getTitle() != null && !request.getTitle().isBlank()
            ? request.getTitle()
            : parsedData.getTitle());

        link.setDescription(request.getDescription() != null && !request.getDescription().isBlank()
            ? request.getDescription()
            : parsedData.getDescription());

        link.setImageUrl(request.getImageUrl() != null && !request.getImageUrl().isBlank()
            ? request.getImageUrl()
            : parsedData.getImageUrl());

        link.setFaviconUrl(parsedData.getFaviconUrl());

        link.setTags(tagService.findTagsByIds(request.getTagIds()));

        Link savedLink = contentItemRepository.save(link);

        linkSearchRepository.save(LinkIndex.fromLink(savedLink));

        return savedLink;
    }

    public ContentItem findByIdAndUser(UUID id, UUID userId) {
        Optional<ContentItem> item = contentItemRepository.findById(id);

        if (item.isEmpty() || !item.get().getUser().getId().equals(userId)) {
            return null;
        }
        return item.get();
    }
}
