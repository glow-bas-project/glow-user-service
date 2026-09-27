# GLOW User Service

The GLOW User Service is a backend microservice responsible for user-related functionality within the GLOW platform. It is independently deployable and forms part of the platform's distributed microservice architecture.

The service is built with **Java 25** and **Quarkus**, using **PostgreSQL** for persistence and **Liquibase** for database migrations. It exposes REST APIs, uses OpenID Connect (OIDC) for authentication and authorization, and includes validation, scheduling, health checks, and fault-tolerance capabilities.

Technologies:
* Java 25 & Quarkus
* PostgreSQL & Liquibase
* REST API
* OpenID Connect (OIDC)
* MapStruct
* Kubernetes
* Gradle
