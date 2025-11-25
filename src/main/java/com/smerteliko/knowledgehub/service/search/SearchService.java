package com.smerteliko.knowledgehub.service.search;

import com.smerteliko.knowledgehub.es.entity.LinkIndex;
import com.smerteliko.knowledgehub.es.entity.NoteIndex;
import com.smerteliko.knowledgehub.entity.ContentItem;
import com.smerteliko.knowledgehub.repository.ContentItemRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import co.elastic.clients.elasticsearch._types.query_dsl.*;


import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ContentItemRepository contentItemRepository;

    private static final int SEARCH_LIMIT = 20;



    /**
     * Performs a full-text search across Note and Link indices for the given user.
     * @param query The search query string.
     * @param userId The ID of the authenticated user.
     * @return A list of matching ContentItem entities.
     */
    public List<ContentItem> searchAllContent(String query, UUID userId) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }

        List<UUID> noteIds = searchIndex(query, userId, NoteIndex.class);

        List<UUID> linkIds = searchIndex(query, userId, LinkIndex.class);

        List<UUID> allIds = new ArrayList<>(noteIds);
        allIds.addAll(linkIds);

        if (allIds.isEmpty()) {
            return new ArrayList<>();
        }

        return contentItemRepository.findAllById(allIds);
    }

    /**
     * Generic method to perform search on a specific index (Note or Link).
     */
    private <T> List<UUID> searchIndex(String query, UUID userId, Class<T> indexClass) {
        Query esQuery = BoolQuery.of(b -> b
            .must(m -> m.term(t -> t
                .field("userId")
                .value(userId.toString())
            ))
            .must(m -> m.multiMatch(mm -> mm
                .fields("title^2.0", "content", "description")
                .query(query)
            ))
        )._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
            .withQuery(esQuery)
            .withPageable(PageRequest.of(0, SEARCH_LIMIT))
            .build();

        SearchHits<T> searchHits = elasticsearchOperations.search(searchQuery, indexClass);

        return searchHits.stream()
            .map(hit -> UUID.fromString(hit.getId()))
            .collect(Collectors.toList());
        }
}
