# Task Management System

A comprehensive web-based task management application built using Java Servlets, JSP, and PostgreSQL. This application follows the MVC (Model-View-Controller) architecture pattern and provides a robust platform for managing tasks, projects, and user assignments.

## Table of Contents

- [Features](#features)
- [Entity Relationship Diagram](#entity-relationship-diagram)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## Features

### User Management
- User registration and authentication
- Role-based access control (Admin and Regular users)
- User profile management

### Project Management
- Create, read, update, and delete projects
- Assign users to projects
- Track project progress and deadlines

### Task Management
- Create, read, update, and delete tasks
- Assign tasks to users
- Set task priorities, deadlines, and statuses
- Filter tasks by various criteria (status, assignee, project, etc.)
- Track task history and changes

### Dashboard
- Overview of tasks and projects
- Task statistics and progress tracking
- Recent activity feed

## Entity Relationship Diagram
![Screenshot 2025-05-16 220901.png](Screenshot%202025-05-16%20220901.png)

## Technologies Used

### Backend
- Java 17
- Jakarta Servlet API 6.0
- JDBC for database connectivity
- PostgreSQL database

### Frontend
- JSP (JavaServer Pages)
- JSTL (JavaServer Pages Standard Tag Library)
- Bootstrap 5 for responsive design
- HTML5, CSS3, JavaScript

### Build Tools
- Maven for dependency management

### Server
- Apache Tomcat 10.1.x

## Project Structure

The project follows the MVC (Model-View-Controller) architecture pattern:

```
Task_Management_System/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── clb/
│   │   │           └── task_management_system/
│   │   │               ├── controller/     # Controllers for business logic
│   │   │               ├── dao/            # Data Access Objects for database operations
│   │   │               ├── filter/         # Servlet filters for authentication and authorization
│   │   │               ├── model/          # Model classes representing data entities
│   │   │               ├── servlet/        # Servlets for handling HTTP requests
│   │   │               └── util/           # Utility classes
│   │   ├── resources/  # Configuration files
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── views/                  # JSP view files
│   │       │   │   ├── auth/               # Authentication views
│   │       │   │   ├── common/             # Common components (header, footer)
│   │       │   │   ├── project/            # Project management views
│   │       │   │   └── task/               # Task management views
│   │       │   └── web.xml                 # Web application configuration
│   │       ├── css/                        # CSS stylesheets
│   │       ├── js/                         # JavaScript files
│   │       └── index.jsp                   # Entry point
│   └── test/                               # Test classes
├── pom.xml                                 # Maven configuration
└── README.md                               # Project documentation
```

## Setup and Installation

### Prerequisites
- JDK 17 or higher
- Apache Maven 3.8.x or higher
- PostgreSQL 14.x or higher
- Apache Tomcat 10.1.x or higher

### Database Setup
1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE task_management_system;
   ```

2. Create a database user:
   ```sql
   CREATE USER task_user WITH ENCRYPTED PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE task_management_system TO task_user;
   ```

3. Run the database schema script:
   ```sql
   -- Create tables
   CREATE TABLE USERS (
       id SERIAL PRIMARY KEY,
       first_name VARCHAR(50) NOT NULL,
       last_name VARCHAR(50) NOT NULL,
       email VARCHAR(100) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       role VARCHAR(20) NOT NULL
   );

   CREATE TABLE PROJECTS (
       id SERIAL PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       description TEXT,
       start_date DATE NOT NULL,
       end_date DATE NOT NULL,
       created_by INTEGER REFERENCES USERS(id)
   );

   CREATE TABLE TASKS (
       id SERIAL PRIMARY KEY,
       title VARCHAR(100) NOT NULL,
       description TEXT,
       status VARCHAR(20) NOT NULL,
       priority VARCHAR(20) NOT NULL,
       due_date DATE NOT NULL,
       created_at TIMESTAMP NOT NULL,
       updated_at TIMESTAMP NOT NULL,
       project_id INTEGER REFERENCES PROJECTS(id),
       assignee_id INTEGER REFERENCES USERS(id),
       created_by INTEGER REFERENCES USERS(id)
   );

   CREATE TABLE TASK_LOGS (
       id SERIAL PRIMARY KEY,
       task_id INTEGER REFERENCES TASKS(id),
       user_id INTEGER REFERENCES USERS(id),
       action VARCHAR(50) NOT NULL,
       old_value TEXT,
       new_value TEXT,
       created_at TIMESTAMP NOT NULL
   );
   ```

4. Insert initial admin user:
   ```sql
   INSERT INTO USERS (first_name, last_name, email, password, role)
   VALUES ('Admin', 'User', 'admin@novatech.com', 'admin123', 'admin');
   ```

### Application Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Task_Management_System.git
   cd Task_Management_System
   ```

2. Configure database connection in `src/main/java/com/clb/task_management_system/util/DatabaseUtil.java`:
   ```java
   private static final String URL = "jdbc:postgresql://localhost:5432/task_management_system";
   private static final String USER = "task_user";
   private static final String PASSWORD = "your_password";
   ```

3. Build the project:
   ```bash
   mvn clean package
   ```

4. Deploy the WAR file to Tomcat:
   - Copy the generated WAR file from `target/Task_Management_System.war` to Tomcat's `webapps` directory
   - Start Tomcat server

## Usage

### Accessing the Application
- Open your web browser and navigate to `http://localhost:8080/Task_Management_System`
- Login with the default admin credentials:
  - Email: admin@novatech.com
  - Password: admin123

### User Roles
1. **Admin**:
   - Manage users, projects, and tasks
   - Create, edit, and delete projects
   - Assign users to tasks
   - View all tasks and projects

2. **Regular User**:
   - View assigned tasks and projects
   - Create and manage tasks
   - Update task status and progress
   - View personal dashboard

### Common Workflows
1. **Creating a New Project** (Admin only):
   - Navigate to Projects
   - Click "New Project"
   - Fill in project details
   - Click "Create Project"

2. **Creating a New Task**:
   - Navigate to Tasks
   - Click "New Task"
   - Fill in task details
   - Assign to a user
   - Click "Create Task"

3. **Updating Task Status**:
   - Navigate to Tasks
   - Click on a task
   - Update the status
   - Click "Save Changes"

## API Documentation

The application does not expose a public API, but the internal API structure follows RESTful principles:

- `/auth` - Authentication operations
- `/dashboard` - Dashboard view
- `/project` - Project management
- `/task` - Task management
- `/users` - User management (admin only)

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

© 2025 NovaTech Solutions. All rights reserved.
