package com.smerteliko.knowledgehub.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestElasticsearchConfig {
//    @Container
//    private static final ElasticsearchContainer ELASTICSEARCH_CONTAINER =
//        new ElasticsearchContainer(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.10"))
//            .withEnv("discovery.type", "single-node")
//            .withEnv("xpack.security.enabled", "false")
//            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");
//
//    static {
//        ELASTICSEARCH_CONTAINER.start();
//    }
//
//    @DynamicPropertySource
//    static void setElasticsearchProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.elasticsearch.uris",
//            () -> ELASTICSEARCH_CONTAINER.getHttpHostAddress());
//        registry.add("spring.elasticsearch.connection-timeout", () -> "30s");
//        registry.add("spring.elasticsearch.socket-timeout", () -> "30s");
//    }
}

