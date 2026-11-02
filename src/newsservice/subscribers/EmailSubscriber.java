package newsservice.subscribers;

import newsservice.core.Subscriber;
import newsservice.model.NewsItem;

public class EmailSubscriber implements Subscriber {
    private final String email;

    public EmailSubscriber(String email) { this.email = email; }

    @Override
    public String getId() { return "email:" + email; }

    @Override
    public void update(NewsItem news) {
        System.out.println("EMAIL to " + email + " -> " + news);
    }
}
