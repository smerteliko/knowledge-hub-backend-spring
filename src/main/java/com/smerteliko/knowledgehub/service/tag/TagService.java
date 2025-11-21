package com.smerteliko.knowledgehub.service.tag;

import com.smerteliko.knowledgehub.entity.Tag;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag createTag(String name, User user) {
        if (tagRepository.findByNameAndUserId(name, user.getId()).isPresent()) {
            throw new RuntimeException("Tag name already exists for this user.");
        }
        Tag tag = new Tag();
        tag.setName(name);
        tag.setUser(user);
        return tagRepository.save(tag);
    }

    public List<Tag> findAllByUserId(UUID userId) {
        return tagRepository.findByUserId(userId);
    }

    /**
     * Finds a set of Tag entities by their IDs.
     * @param tagIds IDs of the tags.
     * @return Set of Tag entities.
     */
    public Set<Tag> findTagsByIds(Set<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Set.of();
        }
        List<Tag> foundTags = tagRepository.findAllById(tagIds);

        if (foundTags.size() != tagIds.size()) {
            System.err.println("Warning: Some requested tags were not found.");
        }

        return Set.copyOf(foundTags);
    }

    public void deleteTagById(UUID tagId, UUID userId) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new RuntimeException("Tag not found."));

        if (!tag.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: User does not own this tag.");
        }

        tagRepository.delete(tag);
    }
}
