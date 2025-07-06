# EasyBudget Backend

A secure and extensible backend service for the EasyBudget personal finance tracker, built with Spring Boot. This backend provides APIs for user authentication, financial entries, category management, and role-based access control.

---

## Features

- JWT-based user authentication
- Role-based access control (Guest, User, Admin)
- CRUD for financial entries, accounts and categories
- Secure password storage with BCrypt
- PostgreSQL for production, H2 for testing
- Dockerized deployment

---

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- PostgreSQL
- H2 (for tests)
- Docker + Docker Compose
- Maven

---

## Running the App Locally (Production Mode)

### Prerequisites

- Java 21+
- Docker & Docker Compose

### Setup

1. **Clone the Repository**

   ```bash
   git clone https://github.com/Npvpr/easybudget-spring.git
    ````

2. **Configure `src/main/resources/application-prod.properties`**

   ```properties
   # PostgreSQL settings
   spring.datasource.url=jdbc:postgresql://eb_db:5432/easybudgetdb
   spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
   spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
   spring.jpa.hibernate.ddl-auto=validate
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

   # JWT secret (referenced from environment variable)
   jwt.secret=${JWT_SECRET}
   ```

3. **Create a `.env` file in the project root**

   ```dotenv
   SPRING_DATASOURCE_USERNAME=your_db_user
   SPRING_DATASOURCE_PASSWORD=your_db_password
   JWT_SECRET=your_jwt_secret
   ```

4. **Build the JAR**

   ```bash
   ./mvnw clean package
   ```

5. **Run with Docker Compose**

   ```bash
   docker compose up --build -d
   ```

---

## Running Tests

### Configure `application-test.properties`

```properties
spring.application.name=easybudget-spring

# H2 in-memory database for testing
spring.datasource.url=jdbc:h2:mem:easybudgetdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.jpa.defer-datasource-initialization=true

# Use a dummy JWT secret for tests
jwt.secret=test_secret
```

> 🔐 No sensitive credentials or environment variables are needed for tests.

### Run the tests

```bash
./mvnw test
```

---

# API Endpoints

## Auth
- `POST /api/auth/signup` – Register a user  
- `POST /api/auth/signin` – Authenticate/login user

## Users
- `GET /api/users/user` – Get the current user info

## Accounts
- `GET /api/accounts` – List all accounts  
- `GET /api/accounts/{accountId}` – Get an account by ID  
- `GET /api/accounts/balance/total` – Get total balance  
- `POST /api/accounts` – Create a new account  
- `PUT /api/accounts/{accountId}` – Update an account  
- `DELETE /api/accounts/{accountId}` – Delete an account

## Categories
- `GET /api/categories` – List all categories  
- `GET /api/categories/{categoryId}` – Get a category by ID  
- `POST /api/categories` – Create a new category  
- `PUT /api/categories/{categoryId}` – Update a category  
- `DELETE /api/categories/{categoryId}` – Delete a category

## Entries
- `GET /api/entries` – List all entries  
- `GET /api/entries/{entryId}` – Get an entry by ID  
- `POST /api/entries` – Create a new entry  
- `PUT /api/entries/{entryId}` – Update an entry  
- `DELETE /api/entries/{entryId}` – Delete an entry

## Graph & Report Endpoints
- `GET /api/entries/monthEntry?year=YYYY&month=MM` – Get entries summary for a given month  
- `GET /api/entries/graphs/month?year=YYYY&month=MM` – Get monthly graph data  
- `GET /api/entries/graphs/year?year=YYYY` – Get yearly graph data  
- `GET /api/entries/graphs/years?startYear=YYYY&endYear=YYYY` – Get graph data across years

## History Filter
- `GET /api/entries/history` – Filter history by optional parameters:
  - `type` (INCOME / OUTCOME)  
  - `accountId`  
  - `categoryId`  
  - `startDate` (ISO 8601)  
  - `endDate` (ISO 8601)  
  - `sortField`, `sortOrder` (asc/desc)

> All protected routes (except auth routes) require the `Authorization: Bearer <token>` header.

