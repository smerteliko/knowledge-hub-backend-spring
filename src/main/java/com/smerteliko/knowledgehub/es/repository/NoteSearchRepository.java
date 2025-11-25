package com.smerteliko.knowledgehub.es.repository;

import com.smerteliko.knowledgehub.es.entity.NoteIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoteSearchRepository extends ElasticsearchRepository<NoteIndex, UUID> {
    // We will use native queries for complex search, but this interface is required
}
