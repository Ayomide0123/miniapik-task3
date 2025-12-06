# Mini API Key & JWT Authentication System

A Spring Boot application implementing **user authentication via JWT** and **service-to-service access via API keys**, designed for secure API access and testing purposes. Includes Swagger/OpenAPI documentation and environment-based configuration for sensitive data.

---

## Features

* **User Authentication**

    * Signup (`/auth/signup`) and login (`/auth/login`)
    * JWT token generation with expiration
    * Secure password storage using BCrypt
* **Service-to-Service Access**

    * API key generation (`/keys/create`)
    * Middleware to detect JWT tokens or API keys
    * Route protection based on access type
    * API key expiration support
* **Clean Code**

    * DTOs and entities use Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`)
    * Minimal boilerplate and readable structure
* **Documentation**

    * Swagger/OpenAPI UI available at `/swagger-ui/index.html`
* **Development Convenience**

    * H2 in-memory database for quick testing
    * Environment-variable-based configuration for sensitive data

---

## Tech Stack & Libraries

* **Spring Boot 3** – Application framework
* **Spring Security** – Authentication and authorization
* **JJWT (0.12.7)** – JWT creation and parsing
* **Lombok** – Auto-generates getters, setters, constructors
* **H2 Database** – In-memory DB for testing
* **Springdoc OpenAPI** – Swagger UI for interactive API docs

---

## Getting Started

### Prerequisites

* Java 17+
* Maven 3.8+
* An IDE (IntelliJ, Eclipse, VS Code)
* Optional: Postman for testing APIs

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/Ayomide0123/miniapik-task3
cd miniapik-task3
```

2. **Set environment variables**

```bash
# Windows CMD
setx JWT_SECRET "your-strong-secret"

# Linux/macOS
export JWT_SECRET="your-strong-secret"
```

3. **Build the project**

```bash
mvn clean install
```

4. **Run the application**

```bash
mvn spring-boot:run
```

---

## Configuration

All sensitive configuration is moved to **environment variables**:

```properties
# application.properties
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=3600000
app.apikey.expiration-days=30
```

* `JWT_SECRET`: The secret used to sign JWT tokens
* `app.jwt.expiration-ms`: JWT token expiration in milliseconds
* `app.apikey.expiration-days`: Default number of days before API keys expire

Database is H2 in-memory, accessible at `/h2-console`:

```text
URL: jdbc:h2:mem:mini
Username: username
Password: password123
```

---

## API Endpoints

### Authentication

| Method | Endpoint     | Description                 |
| ------ | ------------ | --------------------------- |
| POST   | /auth/signup | Create a new user           |
| POST   | /auth/login  | Authenticate and return JWT |

### API Keys

| Method | Endpoint     | Description                        |
| ------ | ------------ | ---------------------------------- |
| POST   | /keys/create | Generate a new API key for service |

### Protected Routes

* Routes can be accessed via **JWT Bearer token** or **API key**.
* Middleware detects the type of access and enforces permissions.

---

## Swagger UI

* Available at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* Explore all endpoints, request/response schemas, and test APIs interactively.

---

## Usage Example

**Login:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin", "password":"password123"}'
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Access protected route with JWT:**

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/protected
```

**Access protected route with API key:**

```bash
curl -H "x-api-key: <apikey>" http://localhost:8080/protected
```

---

## Security Notes

* **JWT secret** is stored in environment variables — never commit to Git.
* **API keys** are random and cryptographically secure.
* **Passwords** are hashed with BCrypt for security.
