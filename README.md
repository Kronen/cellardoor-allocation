# Cellardoor Allocation

Inventory allocation system for managing batches and order lines.

## Features

- Batch and order line management
- REST API with OpenAPI documentation
- Modular architecture
- CI/CD with GitHub Actions

## Quick Start

1. **Requirements**: Java 25, Maven 3.9.11+
2. **Build**: `mvn clean install`
3. **Run**: `mvn spring-boot:run -pl boot`

## API Docs

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Project Structure

```
cellardoor/
├── application/   # Services and DTOs
├── boot/          # App entry point
├── domain/        # Business logic
├── infrastructure/# Database setup
└── rest/          # API endpoints
```
