package newsservice.subscribers;

import newsservice.core.Subscriber;
import newsservice.model.NewsItem;

public class SmsSubscriber implements Subscriber {
    private final String phoneNumber;

    public SmsSubscriber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public String getId() { return "sms:" + phoneNumber; }

    @Override
    public void update(NewsItem news) {
        System.out.println("SMS to " + phoneNumber + " -> " + news.getHeadline());
    }
}
