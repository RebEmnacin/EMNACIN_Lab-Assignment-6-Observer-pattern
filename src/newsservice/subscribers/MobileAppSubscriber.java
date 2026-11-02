package newsservice.subscribers;

import newsservice.core.Subscriber;
import newsservice.model.NewsItem;

public class MobileAppSubscriber implements Subscriber {
    private final String deviceId;
    private final String username;

    public MobileAppSubscriber(String deviceId, String username) {
        this.deviceId = deviceId;
        this.username = username;
    }

    @Override
    public String getId() { return "mobile:" + deviceId; }

    @Override
    public void update(NewsItem news) {
        System.out.println("PUSH to " + username + "'s phone -> " + news);
    }
}
