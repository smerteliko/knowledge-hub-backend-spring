package com.smerteliko.knowledgehub.service.tag;

import com.smerteliko.knowledgehub.entity.Tag;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // ... (Add deletion logic later)
}
