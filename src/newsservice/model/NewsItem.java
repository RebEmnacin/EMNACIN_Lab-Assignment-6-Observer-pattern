package newsservice.model;

import java.time.LocalDateTime;
import java.util.Objects;

/** Immutable news article. Immutability makes it safe to share across threads. */
public final class NewsItem {
    private final String headline;
    private final String body;
    private final NewsCategory category;
    private final LocalDateTime publishedAt;

    public NewsItem(String headline, String body, NewsCategory category) {
        this.headline = Objects.requireNonNull(headline, "headline");
        this.body = Objects.requireNonNull(body, "body");
        this.category = Objects.requireNonNull(category, "category");
        this.publishedAt = LocalDateTime.now();
    }

    public String getHeadline() { return headline; }
    public String getBody() { return body; }
    public NewsCategory getCategory() { return category; }
    public LocalDateTime getPublishedAt() { return publishedAt; }

    @Override
    public String toString() {
        return "[" + category + "] " + headline;
    }
}
