# TaskFlow — To-Do Management System

TaskFlow is a full-stack task management web application built with Java, Spring Boot, PostgreSQL, and Docker.

## About the Project

TaskFlow helps users organize daily tasks, track progress, and manage their work through a modern web interface.

The application combines a REST API, database persistence, user authentication, and an interactive frontend.

## Features

- User registration and login
- User profile management and password change
- Create, view, edit, and delete tasks
- Task priorities and progress tracking
- Kanban board
- Calendar view
- Dashboard and task statistics
- Admin dashboard and user management
- Light and dark themes
- Responsive web interface

## Technologies

- Java 24
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven
- Docker and Docker Compose
- HTML, CSS, and JavaScript
- Swagger / OpenAPI

## Getting Started

### Prerequisites

- Docker Desktop
- Git

### Installation

1. Clone the repository:

```bash
git clone https://github.com/uesherova-eng/todo-management-system.git
cd todo-management-system
```

2. Create a `.env` file in the project root and configure the database password:

```env
DB_PASSWORD=replace_with_your_own_password
```

3. Build and start the application:

```bash
docker compose up --build -d
```

4. Open the web application:

http://localhost:8081/

### API Documentation

http://localhost:8081/swagger-ui/index.html

### Stop the Application

```bash
docker compose down
```

This command stops the containers without intentionally removing the database volume.

## Project Structure

- `src/main/java` — backend code
- `src/main/resources/static` — frontend
- `src/main/resources/db/migration` — database migrations
- `Dockerfile` — application container
- `docker-compose.yml` — application and database services
- `pom.xml` — Maven configuration

## Author

GitHub: [uesherova-eng](https://github.com/uesherova-eng)

## License

Created for educational purposes.
