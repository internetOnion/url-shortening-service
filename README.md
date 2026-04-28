# URL Shortening Service

A REST API for shortening URLs built with Spring Boot 4 (Java 21) and PostgreSQL. Supports creating, retrieving, updating, deleting, and viewing stats for short URLs.

---

## API Documentation

### Overview

All endpoints are served under `/shorten`. Request and response bodies use JSON. The short code is generated via a base62 encoding of a PostgreSQL sequence.

### Endpoints

---

#### 1. Create Short URL

Shortens a given URL and returns the generated short code.

```
POST /shorten
```

**Request Body**

```json
{
  "url": "https://example.com/very/long/url/path"
}
```

| Field | Type   | Required | Description         |
|-------|--------|----------|---------------------|
| `url` | string | yes      | The URL to shorten  |

**Response (`201 Created`)**

```json
{
  "id": "d3e1dd4d-ee40-4ad9-8abf-6b6aaf1c2e21",
  "url": "https://example.com/very/long/url/path",
  "shortCode": "a"
}
```

| Field       | Type   | Description              |
|-------------|--------|--------------------------|
| `id`        | UUID   | Internal identifier      |
| `url`       | string | The original URL         |
| `shortCode` | string | The generated short code |

---

#### 2. Get Short URL

Retrieves a shortened URL by its short code. Increments the access count.

```
GET /shorten/{shortCode}
```

**Path Variable**

| Parameter   | Type   | Required | Description        |
|-------------|--------|----------|--------------------|
| `shortCode` | string | yes      | The short code     |

**Response (`200 OK`)**

```json
{
  "id": "d3e1dd4d-ee40-4ad9-8abf-6b6aaf1c2e21",
  "url": "https://example.com/very/long/url/path",
  "shortCode": "a"
}
```

---

#### 3. Update Short URL

Updates the destination URL for an existing short code.

```
PUT /shorten/{shortCode}
```

**Path Variable**

| Parameter   | Type   | Required | Description        |
|-------------|--------|----------|--------------------|
| `shortCode` | string | yes      | The short code     |

**Request Body**

```json
{
  "url": "https://newexample.com/updated/path"
}
```

**Response (`200 OK`)**

```json
{
  "id": "d3e1dd4d-ee40-4ad9-8abf-6b6aaf1c2e21",
  "url": "https://newexample.com/updated/path",
  "shortCode": "a"
}
```

---

#### 4. Delete Short URL

Deletes a shortened URL by its short code.

```
DELETE /shorten/{shortCode}
```

**Path Variable**

| Parameter   | Type   | Required | Description        |
|-------------|--------|----------|--------------------|
| `shortCode` | string | yes      | The short code     |

**Response (`204 No Content`)**

No response body.

---

#### 5. Get Short URL Statistics

Returns statistics for a short code, including the access count.

```
GET /shorten/{shortCode}/stats
```

**Path Variable**

| Parameter   | Type   | Required | Description        |
|-------------|--------|----------|--------------------|
| `shortCode` | string | yes      | The short code     |

**Response (`200 OK`)**

```json
{
  "id": "d3e1dd4d-ee40-4ad9-8abf-6b6aaf1c2e21",
  "url": "https://example.com/very/long/url/path",
  "shortCode": "a",
  "accessCount": 42
}
```

| Field         | Type   | Description                           |
|---------------|--------|---------------------------------------|
| `id`          | UUID   | Internal identifier                   |
| `url`         | string | The original URL                      |
| `shortCode`   | string | The short code                        |
| `accessCount` | number | Number of times the short URL was accessed |

---

### Error Handling

All error responses follow a consistent structure:

```json
{
  "timestamp": "2026-04-28 12:34:56",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found for shortCode: xyz",
  "validationErrors": null
}
```

| Field              | Type               | Description                                        |
|--------------------|--------------------|----------------------------------------------------|
| `timestamp`        | string             | Timestamp of the error (`yyyy-MM-dd HH:mm:ss`)     |
| `status`           | number             | HTTP status code                                   |
| `error`            | string             | HTTP reason phrase                                 |
| `message`          | string             | Human-readable error description                   |
| `validationErrors` | object or null     | Field-level validation errors (only for `400`)     |

#### Error Responses

| Status | Scenario                              | Example Message / Detail                                      |
|--------|---------------------------------------|---------------------------------------------------------------|
| `400`  | Validation failed (blank URL)         | `"message": "Validation failed"`, `"validationErrors": {"url": "URL is required"}` |
| `400`  | Request body is missing or malformed  | `"message": "Request body is required"`                      |
| `404`  | Short code not found                  | `"message": "Resource not found for shortCode: abc123"`       |
| `500`  | Unexpected server error               | `"message": "An unexpected error occurred"`                  |
