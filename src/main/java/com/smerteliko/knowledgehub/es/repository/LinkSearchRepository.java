package com.smerteliko.knowledgehub.es.repository;

import com.smerteliko.knowledgehub.es.entity.LinkIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LinkSearchRepository extends ElasticsearchRepository<LinkIndex, UUID> {
    // We will use native queries for complex search
}