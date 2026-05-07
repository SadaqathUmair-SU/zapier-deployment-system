# Zapier Deployment System

Java 17 + Spring Boot backend for serving deployment event data.

## Requirements

- Java 17 (OpenJDK 17.x)
- Maven 3.9+ (or use your local `mvn` setup)

## Run Locally (under 2 minutes)

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

## API Endpoints

### List Deployments

```bash
curl "http://localhost:8080/deployments"
```

Optional filters:

```bash
curl "http://localhost:8080/deployments?service=billing-api&status=failed"
```

Response shape:

```json
{
  "data": [
    {
      "id": "deploy_001",
      "service": "billing-api",
      "status": "SUCCESS",
      "duration": 157,
      "timestamp": "2025-04-30T04:00:00Z",
      "commitSha": "01e241"
    }
  ]
}
```

### Get Deployment by ID

```bash
curl "http://localhost:8080/deployments/deploy_001"
```

Response shape:

```json
{
  "data": {
    "id": "deploy_001",
    "service": "billing-api",
    "status": "SUCCESS",
    "duration": 157,
    "timestamp": "2025-04-30T04:00:00Z",
    "commitSha": "01e241"
  }
}
```

If deployment is missing:

```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "Deployment not found for id: deploy_999"
  }
}
```

If status filter is invalid:

```json
{
  "error": {
    "code": "BAD_REQUEST",
    "message": "Invalid status filter: broken. Allowed values: SUCCESS, FAILED, CANCELLED"
  }
}
```

## Seed Data

- 40 deterministic deployment events are seeded at startup
- Services: `billing-api`, `auth-service`, `notifications-worker`, `web-frontend`
- Statuses: `SUCCESS`, `FAILED`, `CANCELLED`

## Run Tests

```bash
mvn test
```
