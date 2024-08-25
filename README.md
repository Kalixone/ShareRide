# 🚗 Overview of Car Sharing Service Online

Welcome to the documentation for the "Car Sharing Service" project, developed using Java Spring Boot.

The Car Sharing Service is a modern, web-based platform designed to manage the entire lifecycle of car rentals. This application provides a streamlined solution for both car sharing service administrators and users, enhancing the efficiency of managing car inventory, rentals, and payments.

With this service, you can perform a wide range of operations, including:

- **Managing Car Inventory:** Add, update, and delete cars in the inventory. Track the number of available cars and their daily rental fees.
- **Handling Car Rentals:** Create and manage rental agreements, including tracking rental and return dates. Ensure car availability and handle rental inventory updates.
- **User Management:** Register new users, update user roles, and manage user profiles. Differentiate between managers and customers to control access to various features.
- **Payment Processing:** Facilitate payments through integration with Stripe. Handle payment sessions, track payment statuses, and manage fines for overdue rentals.
- **Notifications:** Send notifications about new rentals, overdue rentals, and payment statuses using Telegram integration.

---

## 🛠️ Technologies Used

- **Java**: The primary programming language for the entire application.
- **Spring Boot**: Framework enabling rapid and efficient development of Spring-based applications.
- **Stripe** Payment processing platform used for handling car rental payments.
- **Telegram** Messaging service used for notifications.
- **Spring Security**: Provides authentication and authorization mechanisms in the application.
- **JWT (JSON Web Token)**: Standard for securely transmitting information between parties as JSON objects.
- **Spring Data JPA**: Simplifies data access layer by providing repository support and object-relational mapping.
- **Mapstruct**: Tool for automatic mapping between DTOs (Data Transfer Objects) and entities.
- **Lombok**: Library to reduce boilerplate code in Java classes.
- **Maven**: Build automation tool for managing dependencies and building the project.
- **Liquibase**: Manages database schema changes over time.
- **MySQL**: Relational database for storing application data.
- **Docker**: Platform for containerizing applications to ensure consistency between development and production environments.
- **JUnit 5**: Framework for writing unit tests.
- **Mockito**: Framework for creating mocks and stubs in tests.
- **Swagger**: Tool for generating interactive API documentation.

---


## 🚀 Running the Project

To run the project, follow these steps:

### 1. Install Required Tools:

Download and install Docker, Maven, and JDK Development Kit.

### 2. Clone the Project Repository:

Clone the project repository using Git.

### 3. Configure Environment Variables:

In the `.env` file, provide the necessary environment variables related to the database and Docker. Below is an example configuration:

| Variable Name         | Value         |
|-----------------------|---------------|
| MYSQLDB_DATABASE      | cars       |
| MYSQLDB_USER          | admin         |
| MYSQLDB_PASSWORD      | root          |
| MYSQLDB_ROOT_PASSWORD | root          |
| MYSQLDB_LOCAL_PORT    | 3307          |
| MYSQLDB_DOCKER_PORT   | 3306          |
| SPRING_LOCAL_PORT     | 8081          |
| SPRING_DOCKER_PORT    | 8080          |
| DEBUG_PORT            | 5005          |

### 4. Build the Application:
Run the command mvn clean package to build the application.

### 5. Run the Docker Container:
Execute the command docker-compose build to build the Docker container.

Then, use docker-compose up to start the Docker container.

### 6. Accessing the Application:
The application will be available locally at: http://localhost:8081/api.

You can test the application using tools such as Postman or Swagger. For Postman, remember to pass the authentication (Bearer Token) received after logging in.

### 7. Testing Admin and Standard User Features:
- **To test admin features, use the following login credentials:**

Email: admin@example.com,
Password: 123123123

- **For testing standard user features, follow these steps:**

Register a New User:

- **Endpoint: POST /register**
  Request Body: Provide details for email, first name, last name, and password.
  Log In:

- **Endpoint: POST /login**
  Request Body: Use the email and password from the registration step.
  Obtain Bearer Token:

After logging in, you will receive a JWT Bearer token.
Access Standard User Features:

Include the Bearer token in the Authorization header of your requests to access standard user features.

---

## 📊 Entity Structure and Relations Diagram

Below is the entity structure and relations diagram for the "Car Sharing Service Online" project, illustrating connections between various entities and their fields.

![Entity Structure and Relations Diagram](https://i.imgur.com/eaNf3Bu.png)

---

## 📚 Project Structure

**Controllers**
- **AuthenticationController:** Handles user login and registration requests.
- **UsersController:** Manages user-related operations, including profile updates and role management.
- **CarsController:** Oversees car-related operations, such as adding, updating, and deleting cars.
- **PaymentsController:** Handles payment operations, including initiating payment sessions and checking payment statuses. This controller is responsible for creating Stripe payment sessions and handling Stripe redirection through PaymentServiceImpl.
- **RentalsController:** Manages car rentals, including creating new rentals, viewing active rentals, updating return dates, and checking overdue rentals. This controller is responsible for sending notifications about overdue rentals to Telegram via the NotificationService and TelegramNotificationService.

**DTOs (Data Transfer Objects)**
- DTOs, such as RentalDto, RentalRequestDto, UserDto, CarDto, RentalSetActualReturnDateRequestDto, used for transferring data between controllers and services.

**Mappers**
- Mappers, such as RentalMapper, CarMapper, responsible for converting DTO objects to entities and vice versa.

**Services**
- Services, such as RentalService, NotificationService, PaymentService, containing business logic and handling operations related to rentals, notifications, and payments.

**Repositories**
- Repositories, such as CarRepository, RentalRepository, UserRepository, responsible for database communication and data persistence.

**Exceptions**
- Exceptions, such as CarNotFoundException, RentalNotFoundException, UsernameNotFoundException, handling custom errors and exceptions in the application.

**Security**
- Security components, such as JwtAuthenticationFilter, CustomUserDetailsService, JwtUtil, SecurityConfig, handling user authentication and authorization.

**Configuration**
- Application configurations, such as application.properties, liquibase.properties, containing application settings.

**Database Scripts**
- Initialization and update scripts for the database used by Liquibase, such as .yaml and .sql files.

**Tests**
- Unit and integration tests, such as PaymentControllerTest, UserServiceTest, CarRepositoryTest, ensuring the correctness of individual components of the application.

**Infrastructure**
- Docker configurations, such as Dockerfile, docker-compose.yml, used for running the application in Docker containers.

**Other**
- Miscellaneous files and components, such as Swagger Documentation for API reference, Health Check Controller for monitoring application health.

---

## ⭐ Features Overview

### Authentication Management Endpoints
#### Available for Everybody:

🌐 POST: /api/auth/registration - registers a new user.

🌐 POST: /api/auth/login - sign in for an existing user.

### Car Management Endpoints
#### Manager Available:
🔑 POST: /api/cars - creates a new car record.

🔑 PUT: /api/cars/{id} - updates an existing car record.

🔑 DELETE: /api/cars/{id} - deletes a car record.

#### User Available:
👤 GET: /api/cars/{id} - retrieves details of a specific car by ID.

👤 GET: /api/cars - retrieves a list of all cars (paginated).

### Payment Management Endpoints
#### User Available:
👤 POST: /api/payments - creates a new payment session.

👤 GET: /api/payments/{id} - retrieves payments by user ID.

👤 GET: /api/payments/success - handles payment success.

👤 GET: /api/payments/cancel - handles payment cancellation.

### Rental Management Endpoints
#### User Available:
👤 POST: /api/rentals - creates a new rental record.

👤 GET: /api/rentals - retrieves all active rentals for the authenticated user.

👤 GET: /api/rentals/{id} - retrieves details of a specific rental by ID.

👤 POST: /api/rentals/return - sets the actual return date for a rental.

### User Management Endpoints
#### User Available:
👤 GET: /api/users/me - retrieves profile information of the currently authenticated user.

👤 PUT: /api/users/me - updates profile information of the currently authenticated user.

#### Manager Available:
🔑 PUT: /api/users/{id}/role - updates the role of a user by ID.

---

## 📸 Screenshots

Here, you'll find screenshots showcasing the main and most important endpoints of the "Car Sharing Service Online" project. These images highlight the core functionalities of the application. Please note that these are not all the available endpoints. For a complete list of all endpoints, please refer to the section at the top where they are all listed.

### User Registration:
![User Registration](https://i.imgur.com/9b0ui5m.png)
*Screenshot of user registration.*

### User Login:
![User Login](https://i.imgur.com/5JoziCz.png)
*Screenshot of user login.*

### Add New Car:
![Add New Car](https://i.imgur.com/YJ4IOXa.png)
*Screenshot of adding a new car.*

### Retrieve All Cars:
![Retrieve All Cars](https://i.imgur.com/b9TFr1T.png)
*Screenshot of retrieving all cars.*

### Update Car:
![Update Car](https://i.imgur.com/TAdtB4K.png)
*Screenshot of updating an existing car.*

### Get Profile Info:
![Get Profile Info](https://i.imgur.com/WnLYmT4.png)
*Screenshot of viewing profile info.*

### Update Own Profile:
![Update Own Profile](https://i.imgur.com/oxLbb0c.png)
*Screenshot of updating own profile.*

### Rent a Car:
![Rent a Car](https://i.imgur.com/rW1XI0a.png)
*Screenshot of renting a car and receiving a Telegram notification.*

### Set Actual Return Date:
![Set Actual Return Date](https://i.imgur.com/bk406of.png)
*Screenshot of setting the actual return date.*

### Create Payment Session:
![Create Payment Session](https://i.imgur.com/b7IMd3U.png)
*Screenshot of creating a payment session with a redirect to Stripe.*

---

## 🛠 Challenges Faced During Project Development

### Integrating Stripe API and Telegram Bot

Integrating Stripe API and Telegram Bot presented a significant challenge, as I had not previously worked with these technologies in this way. Implementing Stripe for payment processing required setting up payment sessions and handling various payment scenarios, which was a new experience for me. On the other hand, integrating the Telegram Bot to send notifications involved understanding the Telegram API and managing HTTP communications. Both technologies were crucial for the project's functionality but required acquiring new skills and overcoming technical hurdles.
