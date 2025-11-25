package com.smerteliko.knowledgehub.repository;

import com.smerteliko.knowledgehub.entity.ContentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentItemRepository extends JpaRepository<ContentItem, UUID> {
    List<ContentItem> findByUserId(UUID userId);

    // List<ContentItem> findByUserIdAndTags_Id(Long userId, Long tagId);
}