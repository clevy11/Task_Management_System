package com.clb.task_management_system.controller;

import com.clb.task_management_system.dao.UserDAO;
import com.clb.task_management_system.model.User;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Controller class for User-related operations.
 * Part of the Controller component in MVC architecture.
 * Acts as an intermediary between the View (Servlets/JSP) and the Model (DAO/Model classes).
 */
public class UserController {
    private UserDAO userDAO;
    
    public UserController() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Authenticates a user with the provided email and password.
     * 
     * @param email The user's email
     * @param password The user's password
     * @return The authenticated User object if successful, null otherwise
     */
    public User authenticateUser(String email, String password) {
        try {
            User user = userDAO.getUserByEmail(email);
            
            if (user != null && BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified) {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Retrieves a user by their email address.
     * 
     * @param email The email address to search for
     * @return The User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        try {
            return userDAO.getUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Registers a new user with the provided information.
     * 
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param email The user's email
     * @param password The user's password
     * @param confirmPassword The password confirmation
     * @return A map of validation errors (empty if registration is successful)
     */
    public Map<String, String> registerUser(String firstName, String lastName, String email, String password, String confirmPassword) {
        Map<String, String> errors = validateRegistrationInput(firstName, lastName, email, password, confirmPassword);
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        try {
            // Check if email already exists
            if (userDAO.getUserByEmail(email) != null) {
                errors.put("email", "Email already exists");
                return errors;
            }
            
            // Create new user
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(BCrypt.withDefaults().hashToString(12, password.toCharArray()));
            user.setRole("user"); // Default role is user
            
            userDAO.createUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            errors.put("general", "An error occurred during registration");
        }
        
        return errors;
    }
    
    /**
     * Gets a user by their ID.
     * 
     * @param userId The user's ID
     * @return The User object if found, null otherwise
     */
    public User getUserById(int userId) {
        try {
            return userDAO.getUserById(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets all users in the system.
     * 
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        try {
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Updates a user's information.
     * 
     * @param user The User object to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateUser(User user) {
        try {
            return userDAO.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates a user's password.
     * 
     * @param userId The ID of the user
     * @param newPassword The new password
     * @return true if the update was successful, false otherwise
     */
    public boolean updatePassword(int userId, String newPassword) {
        try {
            return userDAO.updatePassword(userId, newPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Validates user registration input.
     * 
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param email The user's email
     * @param password The user's password
     * @param confirmPassword The password confirmation
     * @return A map of validation errors (empty if all inputs are valid)
     */
    private Map<String, String> validateRegistrationInput(String firstName, String lastName, String email, String password, String confirmPassword) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate first name
        if (firstName == null || firstName.trim().isEmpty()) {
            errors.put("firstName", "First name is required");
        } else if (firstName.length() < 2 || firstName.length() > 50) {
            errors.put("firstName", "First name must be between 2 and 50 characters");
        }
        
        // Validate last name
        if (lastName == null || lastName.trim().isEmpty()) {
            errors.put("lastName", "Last name is required");
        } else if (lastName.length() < 2 || lastName.length() > 50) {
            errors.put("lastName", "Last name must be between 2 and 50 characters");
        }
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!isValidEmail(email)) {
            errors.put("email", "Invalid email format");
        }
        
        // Validate password
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password is required");
        } else if (password.length() < 6) {
            errors.put("password", "Password must be at least 6 characters");
        }
        
        // Validate confirm password
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            errors.put("confirmPassword", "Confirm password is required");
        } else if (!password.equals(confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match");
        }
        
        return errors;
    }
    
    /**
     * Checks if an email is valid.
     * 
     * @param email The email to check
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
