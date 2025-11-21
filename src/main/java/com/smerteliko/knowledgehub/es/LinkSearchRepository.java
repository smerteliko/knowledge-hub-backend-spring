package com.smerteliko.knowledgehub.es.repository;

import com.smerteliko.knowledgehub.es.LinkIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface LinkSearchRepository extends ElasticsearchRepository<LinkIndex, UUID> {
    // We will use native queries for complex search
}