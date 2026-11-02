package newsservice.core;

import newsservice.model.NewsCategory;
import newsservice.model.NewsItem;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Concrete Subject (Observer pattern).
 *
 * - ConcurrentHashMap: subscribers can join/leave/change preferences while a publish is running.
 * - Thread pool: each notification runs on its own task, so one slow subscriber
 *   never delays the others and the agency scales with the subscriber count.
 * - try/catch per delivery: one failing subscriber cannot break delivery to the rest.
 */
public class NewsAgency implements NewsPublisher, AutoCloseable {

    private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    public NewsAgency(int deliveryThreads) {
        this.executor = Executors.newFixedThreadPool(deliveryThreads);
    }

    @Override
    public boolean subscribe(Subscriber subscriber) {
        return subscribe(subscriber, EnumSet.allOf(NewsCategory.class));
    }

    @Override
    public boolean subscribe(Subscriber subscriber, Set<NewsCategory> categories) {
        Objects.requireNonNull(subscriber, "subscriber");
        requireNonEmpty(categories);
        return subscriptions.putIfAbsent(subscriber.getId(),
                new Subscription(subscriber, categories)) == null;
    }

    @Override
    public boolean unsubscribe(Subscriber subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");
        return subscriptions.remove(subscriber.getId()) != null;
    }

    @Override
    public boolean updatePreferences(Subscriber subscriber, Set<NewsCategory> categories) {
        Objects.requireNonNull(subscriber, "subscriber");
        requireNonEmpty(categories);
        return subscriptions.replace(subscriber.getId(),
                new Subscription(subscriber, categories)) != null;
    }

    @Override
    public int publish(NewsItem news) {
        Objects.requireNonNull(news, "news");
        int notified = 0;
        for (Subscription sub : subscriptions.values()) {
            if (sub.isInterestedIn(news)) {
                notified++;
                Subscriber target = sub.getSubscriber();
                executor.execute(() -> deliver(target, news));
            }
        }
        return notified;
    }

    public int getSubscriberCount() {
        return subscriptions.size();
    }

    private void deliver(Subscriber target, NewsItem news) {
        try {
            target.update(news);
        } catch (RuntimeException e) {
            System.err.println("Delivery to " + target.getId() + " failed: " + e.getMessage());
        }
    }

    private static void requireNonEmpty(Set<NewsCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("Select at least one category");
        }
    }

    /** Stops accepting new deliveries and waits for pending ones to finish. */
    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
