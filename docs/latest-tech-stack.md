# Latest Online Technology Stack Guide

Date checked: 2026-05-31

This document captures the recommended modern stack for the Camp Microservices Demo and links to official online documentation for each major technology.

## Recommended Tracks

### Track A: Stable Enterprise Track

Use this when the goal is training, compatibility, and lower upgrade risk.

| Layer | Recommendation |
| --- | --- |
| Java | Java 17 minimum, Java 25 compatible |
| Spring Boot | 3.5.x |
| Spring Cloud | 2025.0.x |
| Database | MySQL 8.4 LTS |
| Messaging | Apache Kafka |
| Cache | Redis |
| Search | Elasticsearch or OpenSearch |
| Container Runtime | Docker |
| Orchestration | Kubernetes |
| Observability | Micrometer, Actuator, Prometheus, Grafana, OpenTelemetry |

This is the track currently used by the project because it is stable, Java 17 compatible, and aligned with Spring Cloud 2025.0.x.

### Track B: Latest-Forward Track

Use this when the goal is to teach the latest Spring generation.

| Layer | Recommendation |
| --- | --- |
| Java | Java 25 LTS target, Java 17+ minimum |
| Spring Boot | 4.0.x |
| Spring Cloud | 2025.1.x |
| Spring Framework | 7.x |
| Database | MySQL 8.4 LTS or managed cloud database |
| Messaging | Apache Kafka |
| Cache | Redis 8.x |
| Search | Elasticsearch 9.x or OpenSearch 3.x |
| Container Runtime | Docker / OCI images |
| Orchestration | Kubernetes 1.34+ |
| Observability | OpenTelemetry-first tracing, Micrometer metrics, Prometheus, Grafana |

Spring Cloud maps `2025.1.x` to Spring Boot `4.0.x`, and maps `2025.0.x` to Spring Boot `3.5.x`.

## Project Architecture Stack

| Concern | Technology | Why |
| --- | --- | --- |
| API entry point | Spring Cloud Gateway | Central routing, cross-cutting policies, gateway pattern |
| Service discovery | Eureka for demo, Kubernetes Service DNS for production | Eureka teaches discovery; Kubernetes DNS is production-native |
| Configuration | Spring Cloud Config for demo, Kubernetes ConfigMap/Secret for K8s | Teaches both Spring Cloud and cloud-native config |
| Sync communication | REST + OpenFeign | Used for request/response validation between services |
| API documentation | Springdoc OpenAPI / Swagger UI | Per-service OpenAPI specs and Gateway Swagger aggregation |
| Async communication | Kafka | Used for event-driven Saga choreography |
| Distributed transaction pattern | Choreography Saga | Services react to events without a central orchestrator |
| Transactional event publishing | Outbox table | Reduces risk of DB commit/event publish inconsistency |
| Data storage | MySQL database per service | Strong consistency and ownership per microservice |
| Cache | Redis | Fast lookup cache, not source of truth |
| Search/read index | Elasticsearch/OpenSearch | Search and analytics read model |
| Observability | Actuator, Micrometer, Prometheus, Grafana, OpenTelemetry | Health, metrics, traces, dashboards |
| Deployment | Docker Compose and Kubernetes | Local demo and production-style orchestration |

## Official Online Documentation

| Technology | Official Documentation |
| --- | --- |
| Java 25 | https://docs.oracle.com/javase/specs/ |
| Oracle JDK Downloads | https://www.oracle.com/java/technologies/downloads/ |
| Spring Boot | https://spring.io/projects/spring-boot |
| Spring Boot Reference | https://docs.spring.io/spring-boot/ |
| Spring Cloud | https://spring.io/projects/spring-cloud |
| Spring Cloud Supported Versions | https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions |
| Spring Cloud Gateway | https://docs.spring.io/spring-cloud-gateway/reference/ |
| Spring Cloud OpenFeign | https://docs.spring.io/spring-cloud-openfeign/reference/ |
| Spring for Apache Kafka | https://docs.spring.io/spring-kafka/reference/ |
| Springdoc OpenAPI | https://springdoc.org/ |
| Apache Kafka | https://kafka.apache.org/documentation/ |
| Kafka Downloads | https://kafka.apache.org/downloads/ |
| MySQL | https://dev.mysql.com/doc/ |
| Redis | https://redis.io/docs/latest/ |
| Elasticsearch | https://www.elastic.co/docs |
| OpenSearch | https://docs.opensearch.org/ |
| Docker Compose | https://docs.docker.com/compose/ |
| Compose Specification | https://docs.docker.com/reference/compose-file/ |
| Kubernetes | https://kubernetes.io/docs/ |
| Kubernetes Releases | https://kubernetes.io/releases/ |
| Prometheus | https://prometheus.io/docs/ |
| Grafana | https://grafana.com/docs/ |
| OpenTelemetry | https://opentelemetry.io/docs/ |

## Upgrade Recommendation

For this code camp project:

1. Keep the main branch on Spring Boot `3.5.x` and Spring Cloud `2025.0.x` for a stable enterprise training baseline.
2. Create a separate `boot4` branch for Spring Boot `4.0.x` and Spring Cloud `2025.1.x`.
3. Use Java 25 for local builds and runtime testing, while keeping Java 17 compatibility if enterprise compatibility is required.
4. Prefer Kubernetes-native discovery/config in production, but keep Eureka and Config Server in the training material because they teach Spring Cloud patterns clearly.
5. Prefer managed services in real production: managed MySQL, managed Kafka, managed Redis, and managed Elasticsearch/OpenSearch.

## Production Hardening Checklist

- Replace demo MySQL Deployments with managed MySQL or StatefulSets with persistent volumes.
- Add database migrations with Flyway or Liquibase.
- Replace demo Kafka Deployment with Strimzi, Confluent, Redpanda, or managed Kafka.
- Add OpenTelemetry tracing exporter.
- Add centralized log aggregation.
- Add Kubernetes Ingress or Gateway API instead of NodePort for production.
- Add resource requests and limits.
- Add readiness and liveness probes to every service.
- Add secrets management through Kubernetes Secrets, Vault, or cloud secret manager.
- Add CI/CD image scanning and SBOM generation.
- Add contract tests for synchronous APIs and event schema compatibility tests for Kafka.
