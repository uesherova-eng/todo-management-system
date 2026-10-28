To-Do Management SystemA task management application built with Java, Spring Boot, PostgreSQL, and Docker.About the ProjectThe To-Do Management System is a backend application designed to manage users and their tasks. It provides REST API endpoints for creating, retrieving, updating, and deleting data.Technologies UsedJava 24Spring BootMavenPostgreSQLSpring Data JPAFlyway — database migrationsDocker & Docker ComposeSwagger / OpenAPI — API documentationFeaturesUser managementTask managementREST APIPostgreSQL database integrationDatabase schema migrations with FlywayPassword hashing with BCryptAPI documentation with SwaggerContainerized application using Docker ComposeGetting StartedPrerequisitesDocker DesktopGitRun the ApplicationClone the repository:git clone https://github.com/uesherova-eng/todo-management-system.git
cd todo-management-system
Create a .env file in the project root directory and add:DB_PASSWORD=your_database_password
Replace your_database_password with a password of your choice.Start the application:docker compose up --build -d
Open the API documentation:http://localhost:8081/swagger-ui/index.htmlStop the Applicationdocker compose down
To stop the containers without deleting the database volume, use the command above.Project Structuretodo-management-system/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
Authoruesherova-engLicenseThis project was created for educational purposes.