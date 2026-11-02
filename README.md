# Real-time News Subscription Service

## Problem Statement

**Real-time News Subscription Service**

**Background:** You're tasked with designing a real-time news subscription service for a media company. The system needs to notify subscribers about breaking news as soon as it's published by the news agency.

**Scenario Description:** The system comprises two main components: `NewsAgency` and `Subscriber`.

* **NewsAgency:** Represents the central hub for publishing news. It maintains a list of subscribers and notifies them whenever new news is available.
* **Subscriber:** Represents users or entities subscribed to the news service. Subscribers receive immediate notifications about any breaking news published by the agency.

**Requirements:**

1. **Subscription Management:**
   * Users should be able to subscribe to the news service.
   * Subscribers should be notified promptly when new news is published.
2. **Dynamic Subscription Updates:**
   * The system should allow for dynamic subscription updates. Subscribers can subscribe, unsubscribe, or modify their preferences without disrupting other subscribers.
3. **Decoupled Communication:**
   * Ensure that the communication between the news agency and subscribers is decoupled. Subscribers shouldn't directly request news updates but should be notified by the news agency when new information is available.

**Expected Behavior:**

* When the news agency publishes breaking news, all subscribed users should receive immediate updates.
* Subscribers can join or leave the service without affecting the delivery of news to other subscribers.
* The system should provide flexibility for future enhancements, such as personalized subscriptions or categorization of news updates.

**Constraints:**

* The system should be scalable to handle a growing number of subscribers without compromising performance.
* Ensure that the implementation follows object-oriented design principles and promotes loose coupling between the news agency and subscribers.

---

## Solution: Observer Pattern

`NewsAgency` is the **Subject** and `Subscriber` is the **Observer**. The agency depends only on the `Subscriber` interface, so new subscriber types (email, push, SMS, ...) can be added without touching the agency.

## UML Class Diagram

```mermaid
classDiagram
    direction LR

    class NewsPublisher {
        <<interface>>
        +subscribe(Subscriber) boolean
        +subscribe(Subscriber, Set~NewsCategory~) boolean
        +unsubscribe(Subscriber) boolean
        +updatePreferences(Subscriber, Set~NewsCategory~) boolean
        +publish(NewsItem) int
    }

    class NewsAgency {
        -Map~String, Subscription~ subscriptions
        -ExecutorService executor
        +NewsAgency(int deliveryThreads)
        +getSubscriberCount() int
        -deliver(Subscriber, NewsItem) void
        +close() void
    }

    class Subscriber {
        <<interface>>
        +getId() String
        +update(NewsItem) void
    }

    class Subscription {
        -Subscriber subscriber
        -Set~NewsCategory~ categories
        +getSubscriber() Subscriber
        +isInterestedIn(NewsItem) boolean
    }

    class NewsItem {
        -String headline
        -String body
        -NewsCategory category
        -LocalDateTime publishedAt
        +getHeadline() String
        +getBody() String
        +getCategory() NewsCategory
        +getPublishedAt() LocalDateTime
    }

    class NewsCategory {
        <<enumeration>>
        BREAKING
        POLITICS
        BUSINESS
        TECHNOLOGY
        SPORTS
        ENTERTAINMENT
    }

    class EmailSubscriber {
        -String email
        +update(NewsItem) void
    }
    class MobileAppSubscriber {
        -String deviceId
        -String username
        +update(NewsItem) void
    }
    class SmsSubscriber {
        -String phoneNumber
        +update(NewsItem) void
    }

    NewsPublisher <|.. NewsAgency : implements
    Subscriber <|.. EmailSubscriber : implements
    Subscriber <|.. MobileAppSubscriber : implements
    Subscriber <|.. SmsSubscriber : implements
    NewsAgency "1" o-- "*" Subscription : manages
    Subscription --> "1" Subscriber : holds
    Subscription --> "1..*" NewsCategory : preferences
    NewsItem --> "1" NewsCategory
    NewsAgency ..> NewsItem : publishes
    Subscriber ..> NewsItem : receives
```

## How the Requirements Are Met

| Requirement | How |
|---|---|
| Subscription management | `subscribe(...)` registers a subscriber; `publish(...)` pushes news to them |
| Dynamic updates | `unsubscribe`, `updatePreferences` use a `ConcurrentHashMap` and immutable `Subscription` objects, so changes never disrupt others or in-flight publishes |
| Decoupled communication | Agency only knows the `Subscriber` interface; subscribers never poll, they are pushed to via `update()` |
| Categories / personalization | `NewsCategory` enum + per-subscriber category set; new categories are one line in the enum |
| Scalability | Notifications run on a thread pool, so slow subscribers don't block others; per-delivery `try/catch` isolates failures |
| OOP / loose coupling | Programming to interfaces (`NewsPublisher`, `Subscriber`), immutability, single responsibility per class |

## Project Structure

```
src/newsservice/
├── Main.java
├── model/        NewsItem, NewsCategory
├── core/         NewsPublisher, Subscriber, Subscription, NewsAgency
└── subscribers/  EmailSubscriber, MobileAppSubscriber, SmsSubscriber
```

## Run

Requires JDK 17+.

```bash
mkdir out
javac -d out $(find src -name "*.java")
java -cp out newsservice.Main
```

## Sample Output

```
Subscribers: 3

--- Publishing BREAKING news ---
Notified 2 subscriber(s)
PUSH to Bob's phone -> [BREAKING] Typhoon makes landfall
EMAIL to alice@mail.com -> [BREAKING] Typhoon makes landfall

--- Carla unsubscribes, Bob switches to TECHNOLOGY ---

--- Publishing TECHNOLOGY news ---
EMAIL to alice@mail.com -> [TECHNOLOGY] New chip announced
Notified 2 subscriber(s)
PUSH to Bob's phone -> [TECHNOLOGY] New chip announced

--- Publishing SPORTS news (Bob no longer wants it) ---
EMAIL to alice@mail.com -> [SPORTS] Finals tonight
Notified 1 subscriber(s)
```
(Delivery order between subscribers can vary because notifications are sent concurrently.)
