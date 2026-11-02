package newsservice;

import newsservice.core.NewsAgency;
import newsservice.core.Subscriber;
import newsservice.model.NewsCategory;
import newsservice.model.NewsItem;
import newsservice.subscribers.EmailSubscriber;
import newsservice.subscribers.MobileAppSubscriber;
import newsservice.subscribers.SmsSubscriber;

import java.util.EnumSet;

public class Main {
    public static void main(String[] args) {
        try (NewsAgency agency = new NewsAgency(4)) {

            Subscriber alice = new EmailSubscriber("alice@mail.com");
            Subscriber bob = new MobileAppSubscriber("device-42", "Bob");
            Subscriber carla = new SmsSubscriber("+63-900-000-0000");

            // 1. Subscription management
            agency.subscribe(alice);                                                   // all categories
            agency.subscribe(bob, EnumSet.of(NewsCategory.SPORTS, NewsCategory.BREAKING));
            agency.subscribe(carla, EnumSet.of(NewsCategory.TECHNOLOGY));
            System.out.println("Subscribers: " + agency.getSubscriberCount());

            System.out.println("\n--- Publishing BREAKING news ---");
            int n = agency.publish(new NewsItem("Typhoon makes landfall",
                    "Evacuations underway.", NewsCategory.BREAKING));
            System.out.println("Notified " + n + " subscriber(s)");
            pause(); // let async deliveries print before the next step

            // 2. Dynamic updates: Carla leaves, Bob changes preferences
            System.out.println("\n--- Carla unsubscribes, Bob switches to TECHNOLOGY ---");
            agency.unsubscribe(carla);
            agency.updatePreferences(bob, EnumSet.of(NewsCategory.TECHNOLOGY));

            System.out.println("\n--- Publishing TECHNOLOGY news ---");
            n = agency.publish(new NewsItem("New chip announced",
                    "Faster and cooler.", NewsCategory.TECHNOLOGY));
            System.out.println("Notified " + n + " subscriber(s)");
            pause();

            System.out.println("\n--- Publishing SPORTS news (Bob no longer wants it) ---");
            n = agency.publish(new NewsItem("Finals tonight",
                    "Tip-off at 8 PM.", NewsCategory.SPORTS));
            System.out.println("Notified " + n + " subscriber(s)");
        } // close() waits for pending deliveries
    }

    private static void pause() {
        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
