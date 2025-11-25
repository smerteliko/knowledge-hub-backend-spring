package com.smerteliko.knowledgehub.abstracts;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(/*webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT*/)
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanupDb() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
            "content_item_tags", "links", "notes", "content_items", "tags", "users");
    }

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("knowledgehub_db_test")
            .withUsername("user")
            .withPassword("password");

    @Container
    private static final ElasticsearchContainer ELASTICSEARCH_CONTAINER =
        new ElasticsearchContainer(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.10"))
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL properties
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        // Elasticsearch properties
        registry.add("spring.elasticsearch.uris", ELASTICSEARCH_CONTAINER::getHttpHostAddress);
        registry.add("spring.elasticsearch.connection-timeout", () -> "30s");
        registry.add("spring.elasticsearch.socket-timeout", () -> "30s");

        // JPA properties for test
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
}