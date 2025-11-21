package com.smerteliko.knowledgehub.repository;

import com.smerteliko.knowledgehub.es.NoteIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface NoteSearchRepository extends ElasticsearchRepository<NoteIndex, UUID> {
    // We will use native queries for complex search, but this interface is required
}
