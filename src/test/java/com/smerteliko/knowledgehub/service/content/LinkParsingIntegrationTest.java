package com.smerteliko.knowledgehub.service.content;

import com.smerteliko.knowledgehub.abstracts.AbstractTest;
import com.smerteliko.knowledgehub.dto.content.LinkCreateRequest;
import com.smerteliko.knowledgehub.dto.auth.RegisterRequest;
import com.smerteliko.knowledgehub.entity.Link;
import com.smerteliko.knowledgehub.entity.Tag;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.repository.UserRepository;
import com.smerteliko.knowledgehub.service.auth.AuthService;
import com.smerteliko.knowledgehub.service.tag.TagService; // Убедитесь, что ваш TagService находится в пакете tag
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("integration")
public class LinkParsingIntegrationTest extends AbstractTest {

    @Autowired
    private ContentService contentService;

    @Autowired
    private AuthService authService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Tag testTag1;
    private Tag testTag2;

    private static final String SAMPLE_URL = "https://www.wikipedia.org/";
    private static final String SAMPLE_TITLE_OVERRIDE = "Custom Title Override";


    @BeforeEach
    void setup() {
        if (userRepository.findByEmail("link_test@example.com").isEmpty()) {
            RegisterRequest req = new RegisterRequest();
            req.setUsername("LinkTester");
            req.setEmail("link_test@example.com");
            req.setPassword("testpass");
            authService.register(req);
        }
        testUser = userRepository.findByEmail("link_test@example.com").get();

        testTag1 = tagService.createTag("Wiki", testUser);
        testTag2 = tagService.createTag("Test", testUser);
    }

    @Test
    void testLinkCreationWithParsingAndTags() {
        Set<UUID> tagIds = Set.of(testTag1.getId(), testTag2.getId());

        LinkCreateRequest request = new LinkCreateRequest();
        request.setUrl(SAMPLE_URL);
        request.setTagIds(tagIds);
        request.setTitle(SAMPLE_TITLE_OVERRIDE);

        Link createdLink = (Link) contentService.createLink(request, testUser);

        assertNotNull(createdLink.getId(), "ID ссылки не должен быть null.");

        assertTrue(createdLink.getDescription().length() > 5, "Описание должно быть извлечено парсером.");
        assertTrue(createdLink.getFaviconUrl().contains("wiki"), "Favicon URL должен быть извлечен.");

        assertEquals(SAMPLE_TITLE_OVERRIDE, createdLink.getTitle(), "Заголовок должен быть переопределен DTO.");

        Set<UUID> retrievedTagIds = createdLink.getTags().stream()
            .map(Tag::getId)
            .collect(Collectors.toSet());

        assertEquals(2, createdLink.getTags().size(), "Должно быть привязано ровно 2 тега.");
        assertTrue(retrievedTagIds.containsAll(tagIds), "Привязанные теги должны соответствовать переданным ID.");

    }

    @Test
    void testLinkCreationWithInvalidUrl() {
        LinkCreateRequest request = new LinkCreateRequest();
        request.setUrl("http://invalid-url-that-will-timeout-or-fail-12345.com");

        Link createdLink = (Link) contentService.createLink(request, testUser);

        assertTrue(createdLink.getDescription().contains("Could not connect"),
            "Описание должно содержать сообщение о сбое подключения.");
    }
}