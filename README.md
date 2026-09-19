# 🎓 Smart Campus Assistant

A web-based **Smart Campus Assistant** designed to bring common campus services into a single platform. The application provides features for students and administrators such as campus information, timetable management, attendance-related functionality, notifications, certificates, payments, and other academic/campus services.

The project is developed using **Java and Spring Boot** with **MySQL** as the database and follows a backend-oriented architecture using REST APIs, JPA, Spring Security, and JWT-based authentication.

---

## 📌 Project Overview

Managing different campus activities often requires students to use multiple systems for academic information, schedules, communication, payments, and campus services.

**Smart Campus Assistant** aims to provide a centralized platform where students can access important campus-related services from one application.

The project focuses on:

* Centralized campus services
* Student and administrative workflows
* Secure authentication and authorization
* Timetable and academic information
* Email-based communication
* Certificate generation
* QR-code generation
* Payment-related functionality
* REST API-based backend services
* Database-driven application management

---

## ✨ Features

### 👤 User Authentication & Authorization

* User registration and login
* Secure authentication using Spring Security
* JWT-based authentication
* Role-based access control
* Protected REST API endpoints
* Validation of user input

### 📚 Academic & Campus Services

* Student-related academic information
* Timetable management
* Campus service management
* Structured access to campus-related information
* REST APIs for retrieving and managing application data

### 🗓️ Timetable Management

The project includes timetable-related functionality for managing and accessing class schedules.

The repository also contains documentation for timetable API analysis and testing:

* `TIMETABLE_API_ANALYSIS.md`
* `TIMETABLE_API_TEST_GUIDE.md`

### 📧 Email Services

The application integrates email functionality for communication and notifications.

Possible application workflows include:

* Sending notifications
* Sending account-related emails
* Sending service-related information
* Email-based communication with users

Email credentials are configured through environment variables rather than being stored directly in the source code.

### 📜 Certificate Generation

The application supports certificate-related functionality, including PDF generation for certificates.

PDF generation is implemented using **iText**.

### 🔳 QR Code Generation

QR-code functionality is included for application workflows such as:

* Certificate-related QR codes
* Payment-related QR codes
* Other campus-service use cases

QR-code generation is implemented using **ZXing**.

### 💳 Payment Support

The project includes payment-related functionality and UPI callback handling.

The application contains configuration for payment callback URLs and QR-code generation for UPI-related workflows.

### 🤖 AI Integration

The application includes configuration support for a **Gemini API key**, allowing AI-powered functionality to be integrated into the Smart Campus Assistant.

The Gemini API key is supplied through an environment variable and is not stored directly in the repository.

### 📂 File Uploads

The application supports file-related functionality and provides an upload directory for storing application files.

For deployment, the upload directory should be configured according to the environment rather than relying on a machine-specific Windows path.

---

## 🛠️ Tech Stack

### Backend

* **Java 17**
* **Spring Boot 4.1.0**
* Spring MVC
* Spring Data JPA
* Spring Security
* Spring Validation
* Spring Boot Actuator
* Spring Boot Mail
* JWT Authentication

### Database

* **MySQL**

### Libraries & Tools

* Maven
* Lombok
* JJWT
* ZXing
* iText PDF
* Jackson
* Spring Dotenv
* Git
* GitHub
* Postman

### Deployment

* AWS EC2
* Linux
* Java
* MySQL/MariaDB

---

## 🏗️ Project Architecture

The application follows a layered Spring Boot architecture.

```text
Smart Campus Assistant
│
├── Controller Layer
│   └── Handles HTTP requests and REST APIs
│
├── Service Layer
│   └── Contains application/business logic
│
├── Repository Layer
│   └── Handles database operations using JPA
│
├── Entity/Model Layer
│   └── Represents database entities
│
├── Security Layer
│   └── Authentication, authorization and JWT
│
├── Configuration
│   └── Application and external-service configuration
│
└── Database
    └── MySQL
```

---

## 📁 Repository Structure

```text
smart-campus-assistant/
│
├── .mvn/
│   └── wrapper/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── .env.example
├── .gitignore
├── pom.xml
│
├── TIMETABLE_API_ANALYSIS.md
├── TIMETABLE_API_TEST_GUIDE.md
├── frontend-setup-guide.md
│
├── index.html
├── mvnw
├── mvnw.cmd
│
├── run-and-test.bat
├── run-backend.sh
├── serve-frontend.sh
└── test-timetable-api.bat
```

The repository currently includes Maven wrapper files, backend source code, environment configuration templates, timetable documentation, and scripts for running/testing the application.

---

## 🔐 Environment Configuration

Sensitive information is **not stored directly in the repository**.

The project uses environment variables for values such as:

```text
DB_USERNAME
DB_PASSWORD
MAIL_USERNAME
MAIL_PASSWORD
JWT_SECRET
GEMINI_API_KEY
```

A template is provided in:

```text
.env.example
```

### Example

```env
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

MAIL_USERNAME=your_email
MAIL_PASSWORD=your_app_password

JWT_SECRET=your_jwt_secret

GEMINI_API_KEY=your_gemini_api_key
```

> **Important:** Never commit your real `.env` file, passwords, API keys, JWT secrets, or other credentials to GitHub.

The repository's `.gitignore` is configured to exclude environment files and other sensitive credential/key files.

---

## ⚙️ Prerequisites

Before running the project, make sure you have:

* Java 17 or later
* Maven or Maven Wrapper
* MySQL
* Git
* IntelliJ IDEA or another Java IDE
* Postman (recommended for API testing)

For some features, you may also need:

* Gmail/SMTP credentials
* Gemini API key
* Payment/UPI configuration

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/shirinsinha23/smart-campus-assistant.git
```

Navigate to the project:

```bash
cd smart-campus-assistant
```

---

### 2. Configure the Database

Create a MySQL database:

```sql
CREATE DATABASE smartcampus_db;
```

Create/configure a database user and grant the required permissions.

Then configure:

```env
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
```

---

### 3. Configure Environment Variables

Create your local `.env` file based on:

```text
.env.example
```

Fill in your actual local values.

Do **not** commit `.env` to GitHub.

---

### 4. Configure Application Properties

The application reads sensitive configuration through environment variables.

Example:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

jwt.secret=${JWT_SECRET}

gemini.api.key=${GEMINI_API_KEY}
```

---

### 5. Build the Project

Using Maven Wrapper:

#### Windows

```bash
mvnw.cmd clean package
```

#### Linux/macOS

```bash
./mvnw clean package
```

Or, if Maven is installed:

```bash
mvn clean package
```

---

### 6. Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

Or using the generated JAR:

```bash
java -jar target/smart-campus-assistant-0.0.1-SNAPSHOT.jar
```

The application can then be accessed through the configured server port.

---

## 🌐 API Testing

The backend exposes REST APIs that can be tested using tools such as **Postman**.

Recommended testing flow:

```text
Register/Login
      ↓
Receive authentication credentials
      ↓
Authenticate requests
      ↓
Access protected APIs
      ↓
Test campus services
```

For timetable-related testing, refer to:

```text
TIMETABLE_API_ANALYSIS.md
TIMETABLE_API_TEST_GUIDE.md
```

---

## 🖥️ Frontend

The repository contains frontend-related files and a frontend setup guide.

Refer to:

```text
frontend-setup-guide.md
```

for the project-specific frontend setup and integration instructions.

The frontend communicates with the Spring Boot backend through REST APIs.

---

## ☁️ AWS Deployment

The application can be deployed on an AWS EC2 instance.

A typical deployment flow is:

```text
Local Development
       ↓
Maven Build
       ↓
Executable JAR
       ↓
AWS EC2
       ↓
Java Runtime
       ↓
Spring Boot Application
       ↓
MySQL/MariaDB Database
```

### Basic EC2 Deployment

1. Launch an EC2 instance.
2. Configure the required security-group rules.
3. Install Java.
4. Install/configure MySQL or MariaDB.
5. Upload the generated JAR.
6. Configure environment variables.
7. Start the Spring Boot application.
8. Verify the application and APIs.

Example:

```bash
java -jar smart-campus-assistant-0.0.1-SNAPSHOT.jar
```

For production deployments, use secure configuration management and avoid placing credentials directly in commands, source files, or public repositories.

---

## 🔒 Security Considerations

This project uses several security-related technologies:

* Spring Security
* JWT authentication
* Environment variables
* Input validation
* Role-based authorization

### Secret Management

The following should never be committed with real values:

```text
.env
DB_PASSWORD
MAIL_PASSWORD
JWT_SECRET
GEMINI_API_KEY
```

Use `.env.example` only for documenting the required variable names and placeholder values.

---

## 🧪 Testing

The project contains test dependencies and API testing documentation.

You can test the backend using:

* Spring Boot tests
* Postman
* Timetable API test scripts
* Manual endpoint testing

Available project test-related files include:

```text
TIMETABLE_API_TEST_GUIDE.md
test-timetable-api.bat
```

---

## 📋 Current Project Documentation

The repository contains additional documentation:

| File                          | Purpose                            |
| ----------------------------- | ---------------------------------- |
| `TIMETABLE_API_ANALYSIS.md`   | Timetable API analysis             |
| `TIMETABLE_API_TEST_GUIDE.md` | Timetable API testing instructions |
| `frontend-setup-guide.md`     | Frontend setup and integration     |
| `.env.example`                | Environment-variable template      |

---

## 🔮 Future Enhancements

Potential improvements include:

* Responsive frontend interface
* Improved dashboard experience
* Real-time notifications
* Advanced timetable management
* Attendance analytics
* AI-powered campus assistance
* Improved payment integration
* Cloud-based file storage
* Automated deployment using CI/CD
* HTTPS with a custom domain
* Centralized logging and monitoring
* Improved role and permission management

---

## 🎯 Learning Outcomes

This project provides practical experience with:

* Java backend development
* Spring Boot
* REST API development
* Spring Data JPA
* MySQL database integration
* Spring Security
* JWT authentication
* Email integration
* QR-code generation
* PDF generation
* Environment-variable based configuration
* API testing
* Git and GitHub
* AWS EC2 deployment

---

## 👩‍💻 Developer

**Shirin Sinha**

B.Tech Computer Science & Engineering Student

GitHub:
https://github.com/shirinsinha23

---

## 📄 License

This project is developed for **academic and educational purposes**.

If you intend to reuse or distribute the project, please contact the author and follow the applicable licensing requirements.

---

## ⭐ Acknowledgement

This project was developed as a practical software development project to explore backend development, database management, API development, authentication, and cloud deployment using modern Java technologies.

---

## 📌 Project Status

**Status:** Active Development

The project continues to be improved with additional features, testing, deployment configuration, and documentation.
