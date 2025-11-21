package com.smerteliko.knowledgehub.es;

import com.smerteliko.knowledgehub.entity.Note;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

@Document(indexName = "notes")
@Data
public class NoteIndex {

    @Id
    private UUID id;

    @Field(type = FieldType.Long)
    private UUID userId;

    @Field(type = FieldType.Text, name = "title"/*, boost = 2.0f*/)
    private String title;

    @Field(type = FieldType.Text, name = "content")
    private String content;

    public static NoteIndex fromNote(Note note) {
        NoteIndex index = new NoteIndex();
        index.setId(note.getId());
        index.setUserId(note.getUser().getId());
        index.setTitle(note.getTitle());
        index.setContent(note.getContent());
        return index;
    }
}
