package com.smerteliko.knowledgehub.service.export;

import com.smerteliko.knowledgehub.entity.ContentItem;
import com.smerteliko.knowledgehub.entity.Note;
import com.smerteliko.knowledgehub.entity.Link;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExportService {

    private static final float FONT_SIZE = 12;
    private static final float TITLE_SIZE = 18;
    private static final float LEADING = 1.5f * FONT_SIZE;

    public byte[] exportToPdf(ContentItem item) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, TITLE_SIZE);
                contentStream.beginText();
                contentStream.setLeading(LEADING);

                contentStream.newLineAtOffset(50, 750);
                contentStream.showText(item.getTitle());
                contentStream.newLine();
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
                contentStream.showText("Type: " + item.getContentType());
                contentStream.newLine();
                contentStream.showText("Created: " + item.getCreatedAt().toLocalDate());
                contentStream.newLine();
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);

                if (item instanceof Note note) {
                    String[] lines = note.getContent().split("\n");
                    for (String line : lines) {
                        contentStream.showText(line);
                        contentStream.newLine();
                    }
                } else if (item instanceof Link link) {
                    contentStream.showText("URL: " + link.getUrl());
                    contentStream.newLine();
                    contentStream.showText("Description: " + link.getDescription());
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(out);
            return out.toByteArray();
        }
    }

    public String exportToMarkdown(ContentItem item) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(item.getTitle()).append("\n\n");
        sb.append("> Type: ").append(item.getContentType()).append(" | Created: ").append(item.getCreatedAt().toLocalDate()).append("\n\n");

        if (item instanceof Note note) {
            sb.append(note.getContent()).append("\n");
        } else if (item instanceof Link link) {
            sb.append("## Link Details\n");
            sb.append("* **URL:** ").append(link.getUrl()).append("\n");
            sb.append("* **Description:** ").append(link.getDescription() != null ? link.getDescription() : "N/A").append("\n");
        }

        if (!item.getTags().isEmpty()) {
            sb.append("\n## Tags\n");
            item.getTags().forEach(tag -> sb.append("- `").append(tag.getName()).append("`\n"));
        }

        return sb.toString();
    }
}