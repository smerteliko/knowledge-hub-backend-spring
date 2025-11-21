package com.smerteliko.knowledgehub.service.parse;

import com.smerteliko.knowledgehub.dto.parse.LinkParseResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LinkParsingService {

    private static final int TIMEOUT_MILLIS = 10000; // 10 seconds timeout

    /**
     * Parses the given URL to extract Open Graph metadata (title, description, image).
     * @param url The URL to parse.
     * @return LinkParseResponse DTO containing extracted metadata.
     */
    public LinkParseResponse parseUrl(String url) {
        if (url == null || url.isBlank()) {
            return LinkParseResponse.builder().build();
        }

        try {
            // Jsoup connection setup
            Document doc = Jsoup.connect(url)
                .timeout(TIMEOUT_MILLIS)
                .userAgent("Mozilla/5.0 (compatible; KnowledgeHub/1.0; +https://knowledgehub.com)")
                .get();

            String title = getMetaContent(doc, "og:title");
            String description = getMetaContent(doc, "og:description");
            String imageUrl = getMetaContent(doc, "og:image");

            // Fallback for title/description if OG tags are missing
            if (title.isEmpty()) {
                title = doc.title();
            }
            if (description.isEmpty()) {
                Elements descriptionElement = doc.select("meta[name=description]");
                if (!descriptionElement.isEmpty()) {
                    description = descriptionElement.attr("content");
                }
            }

            // Simple favicon heuristic
            String faviconUrl = doc.select("link[rel~=(?i)icon]").attr("href");
            if (!faviconUrl.startsWith("http") && !faviconUrl.startsWith("//")) {
                faviconUrl = resolveRelativeUrl(url, faviconUrl);
            }

            return LinkParseResponse.builder()
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .faviconUrl(faviconUrl)
                .sourceUrl(url)
                .build();

        } catch (IOException e) {
            System.err.println("Error parsing URL " + url + ": " + e.getMessage());
            return LinkParseResponse.builder()
                .sourceUrl(url)
                .title("Parsing Failed")
                .description("Could not connect or parse content.")
                .build();
        }
    }

    private String getMetaContent(Document doc, String property) {
        Elements elements = doc.select("meta[property=" + property + "]");
        return elements.isEmpty() ? "" : elements.attr("content");
    }

    // Basic helper to resolve relative URLs (e.g., /favicon.ico)
    private String resolveRelativeUrl(String baseUrl, String relativeUrl) {
        if (relativeUrl.isEmpty()) return "";
        try {
            java.net.URL base = new java.net.URL(baseUrl);
            return new java.net.URL(base, relativeUrl).toString();
        } catch (java.net.MalformedURLException e) {
            return relativeUrl;
        }
    }
}
