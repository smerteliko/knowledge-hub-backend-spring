package com.smerteliko.knowledgehub.service.search;

import com.smerteliko.knowledgehub.abstracts.AbstractTest;
import com.smerteliko.knowledgehub.dto.content.NoteCreateRequest;
import com.smerteliko.knowledgehub.dto.auth.RegisterRequest;
import com.smerteliko.knowledgehub.es.entity.LinkIndex;
import com.smerteliko.knowledgehub.es.entity.NoteIndex;
import com.smerteliko.knowledgehub.entity.ContentItem;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.repository.ContentItemRepository;
import com.smerteliko.knowledgehub.repository.UserRepository;
import com.smerteliko.knowledgehub.service.auth.AuthService;
import com.smerteliko.knowledgehub.service.content.ContentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class ContentSearchIntegrationTest extends AbstractTest {

    @Autowired
    private ContentService contentService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentItemRepository contentItemRepository;



    private User testUser;
    private static final String SEARCH_TERM = "ПроверкаПоиска";
    private static final String UNRELATED_TERM = "НеНаходи";

    @BeforeAll
    static void setupIndex(@Autowired ElasticsearchOperations operations) {
        IndexOperations indexOps = operations.indexOps(NoteIndex.class);
        if (indexOps.exists()) {
            indexOps.delete();
        }
        indexOps.create();
        indexOps.putMapping(indexOps.createMapping(NoteIndex.class));

        IndexOperations linkIndexOps = operations.indexOps(LinkIndex.class);
        if (linkIndexOps.exists()) {
            linkIndexOps.delete();
        }
        linkIndexOps.create();
        linkIndexOps.putMapping(linkIndexOps.createMapping(LinkIndex.class));

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void refreshIndex() {
        elasticsearchOperations.indexOps(NoteIndex.class).refresh();
    }

    private User registerTestUser() {
        if (userRepository.findByEmail("search_test@example.com").isEmpty()) {
            RegisterRequest req = new RegisterRequest();
            req.setUsername("SearchUser");
            req.setEmail("search_test@example.com");
            req.setPassword("testpass");
            authService.register(req);
        }
        return userRepository.findByEmail("search_test@example.com").get();
    }

    @Test
    void testFullContentSearchLifecycle() {
        testUser = registerTestUser();

        NoteCreateRequest createRequest = new NoteCreateRequest();
        createRequest.setTitle("Заголовок с " + SEARCH_TERM);
        createRequest.setContent("Это контент для проверки полнотекстового поиска.");

        ContentItem createdItem = contentService.createNote(createRequest, testUser);
        assertNotNull(createdItem);
        UUID createdId = createdItem.getId();
        elasticsearchOperations.indexOps(NoteIndex.class).refresh();

        List<ContentItem> foundItems = searchService.searchAllContent(SEARCH_TERM, testUser.getId());

        assertEquals(1, foundItems.size(), "Должен быть найден один элемент по ключевому слову.");
        assertEquals(createdId, foundItems.get(0).getId());

        List<ContentItem> notFound = searchService.searchAllContent(UNRELATED_TERM, testUser.getId());
        assertTrue(notFound.isEmpty(), "Не должен быть найден элемент по несвязанному слову.");

        contentService.deleteByIdAndUser(createdId, testUser.getId());
        assertTrue(contentItemRepository.findById(createdId).isEmpty(), "Элемент должен быть удален из PostgreSQL.");
        elasticsearchOperations.indexOps(NoteIndex.class).refresh();

        List<ContentItem> afterDelete = searchService.searchAllContent(SEARCH_TERM, testUser.getId());
        assertTrue(afterDelete.isEmpty(), "Элемент должен быть удален из Elasticsearch.");
    }
}
