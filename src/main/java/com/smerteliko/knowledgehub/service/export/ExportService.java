package com.smerteliko.knowledgehub.service.export;

import com.smerteliko.knowledgehub.entity.ContentItem;
import com.smerteliko.knowledgehub.entity.Note;
import com.smerteliko.knowledgehub.entity.Link;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.web.client.RestTemplate;
@Service
public class ExportService {

    private static final float FONT_SIZE = 12;
    private static final float TITLE_SIZE = 18;
    private static final float LEADING = 1.5f * FONT_SIZE;
    private final RestTemplate restTemplate = new RestTemplate();


    public byte[] exportToPdf(ContentItem item) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float currentY = 780;
                float margin = 50;
                float pageWidth = page.getMediaBox().getWidth() - 2 * margin;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, TITLE_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText(item.getTitle());
                contentStream.endText();
                currentY -= LEADING * 2;

                contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.setLeading(LEADING);
                contentStream.showText("Type: " + item.getContentType());
                contentStream.newLine();
                contentStream.showText("Created: " + item.getCreatedAt().toLocalDate());
                contentStream.newLine();
                contentStream.showText("Updated: " + item.getUpdatedAt().toLocalDate());
                contentStream.endText();
                currentY -= LEADING * 3;

                String imageUrl = null;
                if (item instanceof Link link) {
                    imageUrl = link.getImageUrl();
                } else if (item instanceof Note note) {
                    Document doc = Jsoup.parse(note.getContent());
                    imageUrl = doc.select("img").first() != null ? doc.select("img").first().attr("src") : null;
                }

                if (imageUrl != null && imageUrl.startsWith("http")) {
                    try {
                        byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
                        if (imageBytes != null) {
                            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "image");

                            float imageWidth = 200;
                            float imageHeight = (pdImage.getHeight() * imageWidth) / pdImage.getWidth();

                            currentY -= imageHeight + 10;

                            contentStream.drawImage(pdImage, margin, currentY, imageWidth, imageHeight);
                            currentY -= 20;

                        }
                    } catch (Exception e) {
                        System.err.println("Failed to embed image from URL: " + imageUrl + ". Error: " + e.getMessage());
                    }
                }

                contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
                contentStream.beginText();
                contentStream.setLeading(LEADING);
                contentStream.newLineAtOffset(margin, currentY);

                if (item instanceof Note note) {
                    currentY = addWrappedText(contentStream, note.getContent(), margin, currentY, pageWidth, FONT_SIZE);
                } else if (item instanceof Link link) {
                    String linkContent = "URL: " + link.getUrl() + "\nDescription: " + link.getDescription();
                    currentY = addWrappedText(contentStream, linkContent, margin, currentY, pageWidth, FONT_SIZE);
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
        String imageUrl = null;

        if (item instanceof Note note) {
            Document doc = Jsoup.parse(note.getContent());
            imageUrl = doc.select("img").first() != null ? doc.select("img").first().attr("src") : null;

            sb.append(note.getContent()).append("\n");
        } else if (item instanceof Link link) {
            imageUrl = link.getImageUrl();
        }

        if (imageUrl != null) {
            sb.append("\n\n## Preview Image\n");
            sb.append("![Preview](").append(imageUrl).append(")\n");
        }
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

    private float addWrappedText(PDPageContentStream contentStream, String text,
                                 float startX, float startY, float maxWidth, float fontSize) throws IOException {
        PDType1Font font = PDType1Font.HELVETICA;
        String[] paragraphs = text.split("\n");
        float currentY = startY;
        float leading = fontSize * 1.2f;

        for (String paragraph : paragraphs) {
            if (paragraph.trim().isEmpty()) {
                currentY -= leading;
                continue;
            }

            java.util.List<String> lines = wrapText(paragraph, font, fontSize, maxWidth);

            for (String line : lines) {
                if (currentY < 50) {
                    return currentY;
                }

                contentStream.setFont(font, fontSize);
                contentStream.showText(line);
                contentStream.newLine();

                currentY -= leading;
            }

            currentY -= leading * 0.5f;
            contentStream.newLine();
        }

        return currentY;
    }

    private java.util.List<String> wrapText(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        java.util.List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float testWidth = getStringWidth(testLine, font, fontSize);

            if (testWidth <= maxWidth) {
                currentLine.append(currentLine.length() == 0 ? word : " " + word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                    currentLine = new StringBuilder();
                }
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private float getStringWidth(String text, PDType1Font font, float fontSize) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }
}