package newsservice.core;

import newsservice.model.NewsItem;

/**
 * Observer role. The agency only knows about this interface,
 * never about concrete subscriber types (loose coupling).
 */
public interface Subscriber {
    /** Unique identifier used by the agency to track this subscriber. */
    String getId();

    /** Called by the agency (push model) when matching news is published. */
    void update(NewsItem news);
}
