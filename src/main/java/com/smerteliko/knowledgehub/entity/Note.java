package com.smerteliko.knowledgehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notes")
@DiscriminatorValue("NOTE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Note extends ContentItem {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public Note(String title, String content, User user) {
        setTitle(title);
        setContent(content);
        setUser(user);
    }
}
