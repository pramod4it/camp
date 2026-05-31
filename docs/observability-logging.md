# Correlation ID and Central Logging

## Correlation ID

Every HTTP request uses this header:

```text
X-Correlation-Id
```

If the caller does not send one, the service generates a UUID and returns it in the response header.

The same correlation ID is propagated through:

- Gateway HTTP requests
- OpenFeign calls from Order Service to User Service
- Kafka producer headers
- Kafka consumer MDC/log context

## Required Log Fields

All services write structured JSON logs with these fields:

| Field | Meaning |
| --- | --- |
| `@timestamp` | Log event timestamp |
| `service_name` | Spring application name |
| `level` | Log level |
| `thread` | Java thread name |
| `logger` | Logger name |
| `message` | Log message |
| `correlation_id` | End-to-end request/event correlation ID |
| `event_type` | Kafka event class name when present |
| `topic` | Kafka topic when present |
| `order_id` | Order identifier when present |
| `user_id` | User identifier when present |
| `http_method` | HTTP method when present |
| `http_path` | HTTP path when present |

## Kafka Topics

| Topic | Producer | Consumers |
| --- | --- | --- |
| `order-created` | Order Service | Inventory Service, Search Service |
| `inventory-reserved` | Inventory Service | Payment Service, Order Service |
| `inventory-rejected` | Inventory Service | Order Service |
| `payment-processed` | Payment Service | Order Service, Notification Service, Search Service |
| `inventory-release-requested` | Order Service | Inventory Service |

## Dead-Letter Topics

Failed messages are retried 3 times, then published to:

```text
order-created.DLT
inventory-reserved.DLT
inventory-rejected.DLT
payment-processed.DLT
inventory-release-requested.DLT
```

## Elasticsearch Log Indexes

Logstash writes logs to daily per-service indexes:

```text
camp-api-gateway-YYYY.MM.dd
camp-user-service-YYYY.MM.dd
camp-inventory-service-YYYY.MM.dd
camp-order-service-YYYY.MM.dd
camp-payment-service-YYYY.MM.dd
camp-notification-service-YYYY.MM.dd
camp-search-service-YYYY.MM.dd
camp-config-server-YYYY.MM.dd
camp-discovery-server-YYYY.MM.dd
```

Kibana:

```text
http://localhost:5601
```

Kubernetes NodePort:

```text
http://localhost:30601
```

Create a Kibana data view using:

```text
camp-*
```

## Search Service Index

The application search read model still uses:

```text
orders
```

That index is separate from central logging indexes.
