package com.clb.task_management_system.servlet;

import com.clb.task_management_system.controller.UserController;
import com.clb.task_management_system.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet for handling authentication operations like login, logout, and registration.
 * Part of the View component in MVC architecture.
 */
@WebServlet(name = "authServlet", urlPatterns = {"/auth", "/auth/login", "/auth/logout", "/auth/register"})
public class AuthServlet extends HttpServlet {
    private UserController userController;
    
    @Override
    public void init() throws ServletException {
        userController = new UserController();
        
        // Create admin user if it doesn't exist
        try {
            User admin = userController.getUserByEmail("admin@novatech.com");
            if (admin == null) {
                // Create hardcoded admin user
                Map<String, String> result = userController.registerUser(
                    "Admin", 
                    "User", 
                    "admin@novatech.com", 
                    "admin123", 
                    "admin123"
                );
                
                if (result.isEmpty()) {
                    // Registration successful, now update role to admin
                    admin = userController.getUserByEmail("admin@novatech.com");
                    if (admin != null) {
                        admin.setRole("admin");
                        userController.updateUser(admin);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException("Error initializing admin user", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "showLogin";
        }
        
        switch (action) {
            case "showLogin":
                showLoginForm(request, response);
                break;
            case "showRegister":
                showRegisterForm(request, response);
                break;
            case "logout":
                logout(request, response);
                break;
            default:
                showLoginForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        switch (action) {
            case "login":
                login(request, response);
                break;
            case "register":
                register(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
        }
    }
    
    private void showLoginForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    
    private void showRegisterForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }
    
    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validate inputs
        Map<String, String> errors = new HashMap<>();
        
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email is required");
        }
        
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password is required");
        }
        
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }
        
        // Authenticate user using controller
        User user = userController.authenticateUser(email, password);
        
        if (user != null) {
            // Authentication successful
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            // Authentication failed
            request.setAttribute("errorMessage", "Invalid email or password");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
    
    private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Register user using controller
        Map<String, String> errors = userController.registerUser(firstName, lastName, email, password, confirmPassword);
        
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // Registration successful
        request.setAttribute("successMessage", "Registration successful! Please login.");
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
    
    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
    }
}
