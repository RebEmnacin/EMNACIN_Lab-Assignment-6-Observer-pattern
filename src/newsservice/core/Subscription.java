package newsservice.core;

import newsservice.model.NewsCategory;
import newsservice.model.NewsItem;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Immutable pairing of a subscriber and its preferences.
 * Updating preferences means swapping in a NEW Subscription, so
 * in-flight publishes never see half-changed state.
 */
final class Subscription {
    private final Subscriber subscriber;
    private final Set<NewsCategory> categories;

    Subscription(Subscriber subscriber, Set<NewsCategory> categories) {
        this.subscriber = subscriber;
        this.categories = Collections.unmodifiableSet(EnumSet.copyOf(categories));
    }

    Subscriber getSubscriber() { return subscriber; }

    boolean isInterestedIn(NewsItem news) {
        return categories.contains(news.getCategory());
    }
}
