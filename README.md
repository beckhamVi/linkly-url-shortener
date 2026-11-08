# Linkly

Modern URL shortener with a beautiful analytics dashboard.
Built with Spring Boot, Thymeleaf, Redis caching, QR codes and IP-based rate limiting.

## Tech stack
Java 21 · Spring Boot 4 · PostgreSQL · Redis · Flyway · Thymeleaf · Docker

## Getting started
1. Install Java 21 and Docker
2. Clone the repository
3. Start Docker, then run `./gradlew bootRun`
4. Open http://localhost:8080/actuator/health

Spring Boot starts PostgreSQL and Redis automatically from `compose.yaml`.

## Status
🚧 In progress