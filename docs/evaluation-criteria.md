# Evaluation Criteria Mapping

This project is structured to demonstrate enterprise Java microservice design using object-oriented code, SOLID principles, dependency injection, testability, and extensible design patterns.

## 1. Fully Object-Oriented Code

- Domain state and behavior are modeled as classes such as `CustomerOrder`, `InventoryItem`, `Payment`, `Notification`, `OrderDocument`, and `OutboxEvent`.
- API request/response and event payloads are represented as typed records/classes instead of loose maps or string payloads.
- Microservice responsibilities are separated into controllers, listeners, repositories, configuration classes, domain entities, and shared common libraries.
- Shared contracts live in `common-events` and shared technical concerns live in `common-observability`.

## 2. SOLID Principles

### Single Responsibility Principle

- Controllers handle HTTP API concerns.
- Kafka listeners handle asynchronous event consumption.
- Repositories handle persistence contracts.
- Domain objects hold domain state and local behavior.
- `common-observability` owns correlation ID, Kafka metadata, and logging propagation.

### Open/Closed Principle

- New event types can be added in `common-events` without changing existing controllers.
- New services can subscribe to existing Kafka topics without changing the publishing service.
- API paths are centralized in `ApiResource`, so endpoint evolution is controlled in one place.

### Liskov Substitution Principle

- Spring Data repository interfaces are consumed through abstractions, allowing test mocks and runtime implementations to be substituted.
- Kafka and Spring infrastructure dependencies are injected through framework interfaces such as `KafkaTemplate`, `ConsumerFactory`, and repository interfaces.

### Interface Segregation Principle

- Each repository interface is service-specific and exposes only the persistence behavior needed by that service.
- Each microservice owns only its bounded context APIs and persistence model.

### Dependency Inversion Principle

- Controllers and listeners depend on abstractions/interfaces where applicable, especially repositories and infrastructure clients.
- Dependencies are supplied through constructor injection, making classes easy to test with Mockito.

## 3. Design Patterns Used

- **Microservices pattern**: user, inventory, order, payment, notification, and search are separate deployable services.
- **API Gateway pattern**: `api-gateway` provides edge routing.
- **Service Discovery pattern**: `discovery-server` uses Eureka.
- **Externalized Configuration pattern**: `config-server` centralizes service configuration.
- **Saga choreography pattern**: distributed order workflow is coordinated through Kafka events instead of a central transaction coordinator.
- **Outbox pattern**: order and payment services persist outgoing events before publication.
- **Repository pattern**: Spring Data repositories isolate persistence access.
- **DTO/Event contract pattern**: request records and event records define service boundaries.
- **Cross-cutting filter/interceptor pattern**: correlation ID propagation is implemented through servlet filters, reactive filters, Feign interceptors, and Kafka interceptors.
- **Dead-letter topic pattern**: Kafka retry configuration routes failed records to `.DLT` topics.

## 4. Testability And Mocking

- Business logic, controllers, listeners, configuration, utility classes, exception paths, and bootstrap classes are covered by JUnit and Mockito tests.
- Classes use constructor injection, so collaborators can be replaced with Mockito mocks.
- JaCoCo is configured in the parent Maven build and enforces 100% instruction coverage during `mvn verify`.
- Test reports are generated under each module at `target/site/jacoco/index.html`.

## 5. Dependency Injection

- Spring-managed components use constructor injection.
- Infrastructure dependencies such as repositories, Kafka templates, object mappers, consumer factories, and HTTP clients are injected instead of manually created inside business classes.
- Configuration beans are isolated in focused configuration classes such as `OpenApiConfig` and `KafkaRetryConfig`.
- Shared observability beans are provided through `ObservabilityAutoConfiguration`.

## 6. Extensibility

- New services can be added as Maven modules and wired into Docker/Kubernetes independently.
- New API versions can be introduced through `ApiResource` constants such as `/api/v1` and `/api/v2`.
- New Kafka topics can be added through `KafkaTopics`.
- New Saga steps can be added by publishing/subscribing to event records in `common-events`.
- New database scripts can be added under `sql/<database-name>`.
- New Postman collections can be added under `postman/<microservice-name>`.

## Notes

- The current project does not include an authentication/authorization module. If Spring Security is added later, the same constructor-injection and test-first approach should be used for filters, token services, role checks, and failure handlers.
- The project currently verifies with `mvn verify` and a 100% JaCoCo instruction coverage gate.
