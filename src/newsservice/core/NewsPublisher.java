package newsservice.core;

import newsservice.model.NewsCategory;
import newsservice.model.NewsItem;

import java.util.Set;

/** Subject role: what any news source must be able to do. */
public interface NewsPublisher {
    /** Subscribe to ALL categories. Returns false if already subscribed. */
    boolean subscribe(Subscriber subscriber);

    /** Subscribe to selected categories only. Returns false if already subscribed. */
    boolean subscribe(Subscriber subscriber, Set<NewsCategory> categories);

    /** Remove a subscriber. Returns false if they were not subscribed. */
    boolean unsubscribe(Subscriber subscriber);

    /** Change the categories of an existing subscriber. Returns false if not subscribed. */
    boolean updatePreferences(Subscriber subscriber, Set<NewsCategory> categories);

    /** Publish news and push it to every interested subscriber. Returns how many were notified. */
    int publish(NewsItem news);
}
