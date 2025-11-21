package com.smerteliko.knowledgehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "links")
@DiscriminatorValue("LINK")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Link extends ContentItem {

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private String faviconUrl;
}