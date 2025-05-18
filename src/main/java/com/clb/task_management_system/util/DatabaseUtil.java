package com.clb.task_management_system.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for database operations.
 * Part of the Model component in MVC architecture.
 */
public class DatabaseUtil {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/Task_management_system";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "728728";
    
    private static boolean tablesInitialized = false;

    /**
     * Gets a connection to the database and initializes tables if needed.
     * 
     * @return A database connection
     * @throws SQLException If a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            
            // Initialize tables if not already done
            if (!tablesInitialized) {
                initializeTables(connection);
                tablesInitialized = true;
            }
            
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Initializes database tables if they don't exist.
     * 
     * @param connection The database connection
     */
    private static void initializeTables(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            // Create users table if not exists
            stmt.execute("CREATE TABLE IF NOT EXISTS USERS (" +
                    "id SERIAL PRIMARY KEY, " +
                    "first_name VARCHAR(50) NOT NULL, " +
                    "last_name VARCHAR(50) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(10) NOT NULL DEFAULT 'user' CHECK (role IN ('admin', 'user'))" +
                    ")");
            
            // Create projects table if not exists
            stmt.execute("CREATE TABLE IF NOT EXISTS PROJECTS (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "description TEXT, " +
                    "start_date DATE, " +
                    "end_date DATE, " +
                    "created_by INT NOT NULL, " +
                    "FOREIGN KEY (created_by) REFERENCES USERS(id)" +
                    ")");
            
            // Create tasks table if not exists
            stmt.execute("CREATE TABLE IF NOT EXISTS TASKS (" +
                    "id SERIAL PRIMARY KEY, " +
                    "title VARCHAR(100) NOT NULL, " +
                    "description TEXT, " +
                    "due_date DATE, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'Pending' CHECK (status IN ('Pending', 'In Progress', 'Completed')), " +
                    "assigned_to INT NOT NULL, " +
                    "project_id INT, " +
                    "created_by INT NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (assigned_to) REFERENCES USERS(id), " +
                    "FOREIGN KEY (project_id) REFERENCES PROJECTS(id), " +
                    "FOREIGN KEY (created_by) REFERENCES USERS(id)" +
                    ")");
            
            // Create task_logs table if not exists
            stmt.execute("CREATE TABLE IF NOT EXISTS TASK_LOGS (" +
                    "id SERIAL PRIMARY KEY, " +
                    "task_id INT NOT NULL, " +
                    "old_status VARCHAR(50), " +
                    "new_status VARCHAR(50), " +
                    "changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "changed_by INT NOT NULL, " +
                    "FOREIGN KEY (task_id) REFERENCES TASKS(id), " +
                    "FOREIGN KEY (changed_by) REFERENCES USERS(id)" +
                    ")");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Closes a database connection.
     * 
     * @param connection The connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
