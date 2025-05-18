package com.clb.task_management_system.servlet;

import com.clb.task_management_system.controller.ProjectController;
import com.clb.task_management_system.controller.TaskController;
import com.clb.task_management_system.controller.UserController;
import com.clb.task_management_system.model.Project;
import com.clb.task_management_system.model.Task;
import com.clb.task_management_system.model.TaskLog;
import com.clb.task_management_system.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Servlet for handling task-related operations.
 * Part of the View component in MVC architecture.
 */
@WebServlet(name = "taskServlet", urlPatterns = {"/tasks", "/task/create", "/task/edit/*", "/task/delete/*", "/task/view/*", "/task/status/*"})
public class TaskServlet extends HttpServlet {
    
    private TaskController taskController;
    private ProjectController projectController;
    private UserController userController;
    
    @Override
    public void init() throws ServletException {
        taskController = new TaskController();
        projectController = new ProjectController();
        userController = new UserController();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        
        // Get current user
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            // User not logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        switch (path) {
            case "/tasks":
                handleListTasks(request, response);
                break;
                
            case "/task/create":
                // Get all users for assignment
                List<User> users = userController.getAllUsers();
                request.setAttribute("users", users);
                
                // Get all projects
                List<Project> projects = projectController.getAllProjects();
                request.setAttribute("projects", projects);
                
                request.getRequestDispatcher("/WEB-INF/views/task/create.jsp").forward(request, response);
                break;
                
            case "/task/edit":
                if (pathInfo != null && pathInfo.length() > 1) {
                    try {
                        int taskId = Integer.parseInt(pathInfo.substring(1));
                        Task task = taskController.getTaskById(taskId);
                        
                        if (task != null) {
                            // Check if user is authorized to edit this task
                            boolean isCreator = task.getCreatedBy() == currentUser.getId();
                            boolean isAssignee = task.getAssignedTo() == currentUser.getId();
                            boolean isAdmin = "admin".equals(currentUser.getRole());
                            
                            if (isCreator || isAssignee || isAdmin) {
                                request.setAttribute("task", task);
                                
                                // Get all users for assignment
                                users = userController.getAllUsers();
                                request.setAttribute("users", users);
                                
                                // Get all projects
                                projects = projectController.getAllProjects();
                                request.setAttribute("projects", projects);
                                
                                request.getRequestDispatcher("/WEB-INF/views/task/edit.jsp").forward(request, response);
                                return;
                            } else {
                                response.sendRedirect(request.getContextPath() + "/dashboard?error=unauthorized");
                                return;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Invalid task ID
                    }
                }
                
                response.sendRedirect(request.getContextPath() + "/tasks?error=invalid");
                break;
                
            case "/task/delete":
                if (pathInfo != null && pathInfo.length() > 1) {
                    try {
                        int taskId = Integer.parseInt(pathInfo.substring(1));
                        Task task = taskController.getTaskById(taskId);
                        
                        if (task != null) {
                            // Check if user is authorized to delete this task
                            boolean isCreator = task.getCreatedBy() == currentUser.getId();
                            boolean isAdmin = "admin".equals(currentUser.getRole());
                            
                            if (isCreator || isAdmin) {
                                boolean success = taskController.deleteTask(taskId);
                                
                                if (success) {
                                    response.sendRedirect(request.getContextPath() + "/tasks?success=deleted");
                                    return;
                                }
                            } else {
                                response.sendRedirect(request.getContextPath() + "/dashboard?error=unauthorized");
                                return;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Invalid task ID
                    }
                }
                
                response.sendRedirect(request.getContextPath() + "/tasks?error=delete");
                break;
                
            case "/task/view":
                if (pathInfo != null && pathInfo.length() > 1) {
                    try {
                        int taskId = Integer.parseInt(pathInfo.substring(1));
                        Task task = taskController.getTaskById(taskId);
                        
                        if (task != null) {
                            request.setAttribute("task", task);
                            
                            // Get task logs
                            List<TaskLog> logs = taskController.getTaskLogs(taskId);
                            request.setAttribute("logs", logs);
                            
                            request.getRequestDispatcher("/WEB-INF/views/task/view.jsp").forward(request, response);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        // Invalid task ID
                    }
                }
                
                response.sendRedirect(request.getContextPath() + "/tasks?error=invalid");
                break;
                
            case "/task/status":
                if (pathInfo != null && pathInfo.length() > 1) {
                    try {
                        int taskId = Integer.parseInt(pathInfo.substring(1));
                        Task task = taskController.getTaskById(taskId);
                        
                        if (task != null) {
                            // Check if user is authorized to change status
                            boolean isAssignee = task.getAssignedTo() == currentUser.getId();
                            boolean isCreator = task.getCreatedBy() == currentUser.getId();
                            boolean isAdmin = "admin".equals(currentUser.getRole());
                            
                            if (isAssignee || isCreator || isAdmin) {
                                request.setAttribute("task", task);
                                request.getRequestDispatcher("/WEB-INF/views/task/status.jsp").forward(request, response);
                                return;
                            } else {
                                response.sendRedirect(request.getContextPath() + "/dashboard?error=unauthorized");
                                return;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Invalid task ID
                    }
                }
                
                response.sendRedirect(request.getContextPath() + "/tasks?error=invalid");
                break;
                
            default:
                response.sendRedirect(request.getContextPath() + "/tasks");
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        
        // Get current user
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            // User not logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        switch (path) {
            case "/task/create":
                handleCreateTask(request, response, currentUser);
                break;
                
            case "/task/edit":
                if (pathInfo != null && pathInfo.length() > 1) {
                    try {
                        int taskId = Integer.parseInt(pathInfo.substring(1));
                        Task task = taskController.getTaskById(taskId);
                        
                        if (task != null) {
                            // Check if user is authorized to edit this task
                            boolean isCreator = task.getCreatedBy() == currentUser.getId();
                            boolean isAssignee = task.getAssignedTo() == currentUser.getId();
                            boolean isAdmin = "admin".equals(currentUser.getRole());
                            
                            if (isCreator || isAssignee || isAdmin) {
                                handleUpdateTask(request, response, taskId);
                                return;
                            } else {
                                response.sendRedirect(request.getContextPath() + "/dashboard?error=unauthorized");
                                return;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Invalid task ID
                    }
                }
                
                response.sendRedirect(request.getContextPath() + "/tasks?error=invalid");
                break;
                
            case "/task/status":
                if (pathInfo != null && pathInfo.length() > 1) {
                    try {
                        int taskId = Integer.parseInt(pathInfo.substring(1));
                        Task task = taskController.getTaskById(taskId);
                        
                        if (task != null) {
                            // Check if user is authorized to change status
                            boolean isAssignee = task.getAssignedTo() == currentUser.getId();
                            boolean isCreator = task.getCreatedBy() == currentUser.getId();
                            boolean isAdmin = "admin".equals(currentUser.getRole());
                            
                            if (isAssignee || isCreator || isAdmin) {
                                handleUpdateStatus(request, response, taskId, currentUser);
                                return;
                            } else {
                                response.sendRedirect(request.getContextPath() + "/dashboard?error=unauthorized");
                                return;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Invalid task ID
                    }
                }
                
                response.sendRedirect(request.getContextPath() + "/tasks?error=invalid");
                break;
                
            default:
                response.sendRedirect(request.getContextPath() + "/tasks");
                break;
        }
    }
    
    private void handleListTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get current user
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        List<Task> tasks;
        
        // Admin can see all tasks, regular users only see their assigned tasks
        if (currentUser.isAdmin()) {
            tasks = taskController.getAllTasks();
        } else {
            tasks = taskController.getTasksByAssignee(currentUser.getId());
        }
        
        // Get filter parameters
        String statusFilter = request.getParameter("status");
        String projectFilter = request.getParameter("project");
        
        // Apply filters if provided
        if (statusFilter != null && !statusFilter.isEmpty() && tasks != null) {
            // Filter by status - we need to filter the already fetched tasks
            tasks.removeIf(task -> !task.getStatus().equals(statusFilter));
            request.setAttribute("statusFilter", statusFilter);
        }
        
        if (projectFilter != null && !projectFilter.isEmpty()) {
            try {
                int projectId = Integer.parseInt(projectFilter);
                tasks = taskController.getTasksByProject(projectId);
                
                // If we're also filtering by status, we need to apply that filter too
                if (statusFilter != null && !statusFilter.isEmpty() && tasks != null) {
                    tasks.removeIf(task -> !task.getStatus().equals(statusFilter));
                }
                
                request.setAttribute("projectFilter", projectId);
            } catch (NumberFormatException e) {
                // Invalid project ID, ignore filter
            }
        }
        
        request.setAttribute("tasks", tasks);
        
        // Get all projects for filter dropdown
        List<Project> projects = projectController.getAllProjects();
        request.setAttribute("projects", projects);
        
        request.getRequestDispatcher("/WEB-INF/views/task/list.jsp").forward(request, response);
    }
    
    private void handleCreateTask(HttpServletRequest request, HttpServletResponse response, User currentUser) throws ServletException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String dueDateStr = request.getParameter("dueDate");
        String assignedToStr = request.getParameter("assignedTo");
        String projectIdStr = request.getParameter("projectId");
        
        // Parse due date if provided
        Date dueDate = null;
        try {
            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                dueDate = Date.valueOf(dueDateStr);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid date format");
            
            // Get all users for assignment
            List<User> users = userController.getAllUsers();
            request.setAttribute("users", users);
            
            // Get all projects
            List<Project> projects = projectController.getAllProjects();
            request.setAttribute("projects", projects);
            
            request.getRequestDispatcher("/WEB-INF/views/task/create.jsp").forward(request, response);
            return;
        }
        
        // Parse assignee
        int assignedTo;
        try {
            assignedTo = Integer.parseInt(assignedToStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid assignee");
            
            // Get all users for assignment
            List<User> users = userController.getAllUsers();
            request.setAttribute("users", users);
            
            // Get all projects
            List<Project> projects = projectController.getAllProjects();
            request.setAttribute("projects", projects);
            
            request.getRequestDispatcher("/WEB-INF/views/task/create.jsp").forward(request, response);
            return;
        }
        
        // Parse project ID if provided
        Integer projectId = null;
        if (projectIdStr != null && !projectIdStr.trim().isEmpty()) {
            try {
                projectId = Integer.parseInt(projectIdStr);
            } catch (NumberFormatException e) {
                // Invalid project ID, leave as null
            }
        }
        
        // Create task using controller
        Map<String, String> errors = taskController.createTask(
            title, 
            description, 
            dueDate, 
            assignedTo, 
            projectId, 
            currentUser.getId()
        );
        
        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/tasks?success=created");
        } else {
            // Set error messages
            for (Map.Entry<String, String> error : errors.entrySet()) {
                request.setAttribute(error.getKey() + "Error", error.getValue());
            }
            
            // Set form values for redisplay
            request.setAttribute("title", title);
            request.setAttribute("description", description);
            request.setAttribute("dueDate", dueDateStr);
            request.setAttribute("assignedTo", assignedToStr);
            request.setAttribute("projectId", projectIdStr);
            
            // Get all users for assignment
            List<User> users = userController.getAllUsers();
            request.setAttribute("users", users);
            
            // Get all projects
            List<Project> projects = projectController.getAllProjects();
            request.setAttribute("projects", projects);
            
            request.getRequestDispatcher("/WEB-INF/views/task/create.jsp").forward(request, response);
        }
    }
    
    private void handleUpdateTask(HttpServletRequest request, HttpServletResponse response, int taskId) throws ServletException, IOException {
        // Get existing task
        Task task = taskController.getTaskById(taskId);
        
        if (task == null) {
            response.sendRedirect(request.getContextPath() + "/tasks?error=invalid");
            return;
        }
        
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String dueDateStr = request.getParameter("dueDate");
        String status = request.getParameter("status");
        String assignedToStr = request.getParameter("assignedTo");
        String projectIdStr = request.getParameter("projectId");
        
        // Parse due date if provided
        try {
            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                task.setDueDate(Date.valueOf(dueDateStr));
            } else {
                task.setDueDate(null);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid date format");
            request.setAttribute("task", task);
            
            // Get all users for assignment
            List<User> users = userController.getAllUsers();
            request.setAttribute("users", users);
            
            // Get all projects
            List<Project> projects = projectController.getAllProjects();
            request.setAttribute("projects", projects);
            
            request.getRequestDispatcher("/WEB-INF/views/task/edit.jsp").forward(request, response);
            return;
        }
        
        // Update task fields
        task.setTitle(title);
        task.setDescription(description);
        
        // Set status
        if (status != null && !status.trim().isEmpty()) {
            task.setStatus(status);
        }
        
        // Set assignee
        try {
            int assignedTo = Integer.parseInt(assignedToStr);
            task.setAssignedTo(assignedTo);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid assignee");
            request.setAttribute("task", task);
            
            // Get all users for assignment
            List<User> users = userController.getAllUsers();
            request.setAttribute("users", users);
            
            // Get all projects
            List<Project> projects = projectController.getAllProjects();
            request.setAttribute("projects", projects);
            
            request.getRequestDispatcher("/WEB-INF/views/task/edit.jsp").forward(request, response);
            return;
        }
        
        // Set project if provided
        if (projectIdStr != null && !projectIdStr.trim().isEmpty()) {
            try {
                int projectId = Integer.parseInt(projectIdStr);
                task.setProjectId(projectId);
            } catch (NumberFormatException e) {
                // Invalid project ID, leave as is
            }
        } else {
            task.setProjectId(0); // No project
        }
        
        // Get current user for the update log
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        // Update task using controller
        Map<String, String> errors = taskController.updateTask(task, currentUser.getId());
        
        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/tasks?success=updated");
        } else {
            // Set error messages
            for (Map.Entry<String, String> error : errors.entrySet()) {
                request.setAttribute(error.getKey() + "Error", error.getValue());
            }
            
            request.setAttribute("task", task);
            
            // Get all users for assignment
            List<User> users = userController.getAllUsers();
            request.setAttribute("users", users);
            
            // Get all projects
            List<Project> projects = projectController.getAllProjects();
            request.setAttribute("projects", projects);
            
            request.getRequestDispatcher("/WEB-INF/views/task/edit.jsp").forward(request, response);
        }
    }
    
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response, int taskId, User currentUser) throws ServletException, IOException {
        String newStatus = request.getParameter("newStatus");
        
        // Validate input
        if (newStatus == null || newStatus.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Status is required");
            
            // Get task
            Task task = taskController.getTaskById(taskId);
            request.setAttribute("task", task);
            
            request.getRequestDispatcher("/WEB-INF/views/task/status.jsp").forward(request, response);
            return;
        }
        
        // Update task status using controller
        boolean success = taskController.updateTaskStatus(taskId, newStatus, currentUser.getId());
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/task/view/" + taskId + "?success=status");
        } else {
            request.setAttribute("errorMessage", "Failed to update task status");
            
            // Get task
            Task task = taskController.getTaskById(taskId);
            request.setAttribute("task", task);
            
            request.getRequestDispatcher("/WEB-INF/views/task/status.jsp").forward(request, response);
        }
    }
}
