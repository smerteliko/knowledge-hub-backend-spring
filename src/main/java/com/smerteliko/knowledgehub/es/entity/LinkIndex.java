package com.smerteliko.knowledgehub.es.entity;

import com.smerteliko.knowledgehub.entity.Link;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

@Document(indexName = "links")
@Data
public class LinkIndex {

    @Id
    private String id;

    @Field(type = FieldType.Keyword, name = "userId")
    private UUID userId;

    @Field(type = FieldType.Text, name = "title"/*, boost = 2.0f*/)
    private String title;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Keyword, name = "url")
    private String url;

    public static LinkIndex fromLink(Link link) {
        LinkIndex index = new LinkIndex();
        index.setId(link.getId().toString());
        index.setUserId(link.getUser().getId());
        index.setTitle(link.getTitle());
        index.setDescription(link.getDescription());
        index.setUrl(link.getUrl());
        return index;
    }
}