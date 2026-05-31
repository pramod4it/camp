# Architecture

## Runtime Contract

- Minimum Java runtime: 17
- Current target runtime: 25
- Build bytecode release: 17
- Framework baseline: Spring Boot 3.5.x and Spring Cloud 2025.0.x

## Services

| Service | Responsibility | Store | Communication |
| --- | --- | --- | --- |
| API Gateway | External routing | None | HTTP |
| User Service | User management | MySQL + Redis cache | HTTP |
| Inventory Service | Stock reservation and compensation release | MySQL | HTTP + Kafka |
| Order Service | Order creation and status | MySQL + outbox | HTTP + Kafka |
| Payment Service | Payment processing | MySQL | Kafka |
| Notification Service | Notification history | MySQL | Kafka |
| Search Service | Search/read index | Elasticsearch | Kafka |

## Technology Rules

- MySQL is the source of truth for transactional data.
- Kafka carries domain events between services.
- Redis is a cache, not the source of truth.
- Elasticsearch supports search and analytics, not payment/order transactions.
- Actuator and Prometheus metrics are enabled for operational visibility.
- Order Service uses OpenFeign for synchronous user validation.
- Order and Payment services use an outbox table before publishing Saga events.
- Kafka consumers use retries and dead-letter topics with the `.DLT` suffix.
- HTTP, OpenFeign, Kafka headers, and structured logs use `X-Correlation-Id`.
- Logstash indexes JSON logs into Elasticsearch indexes named `camp-{service_name}-YYYY.MM.dd`.

## Choreography Saga

1. Order Service validates the user over HTTP and creates an order.
2. Order Service publishes `OrderCreatedEvent`.
3. Inventory Service reserves stock and publishes `InventoryReservedEvent`, or rejects with `InventoryRejectedEvent`.
4. Payment Service processes only reserved orders and publishes `PaymentProcessedEvent`.
5. Order Service marks the order paid, or marks payment failed and publishes `InventoryReleaseRequestedEvent`.
6. Inventory Service releases reserved stock when compensation is requested.
