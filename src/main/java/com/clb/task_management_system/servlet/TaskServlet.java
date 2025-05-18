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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        try {
            switch (path) {
                case "/tasks":
                    handleListTasks(request, response, currentUser);
                    break;
                    
                case "/task/create":
                    prepareTaskForm(request);
                    request.getRequestDispatcher("/WEB-INF/views/task/create.jsp").forward(request, response);
                    break;
                    
                case "/task/view":
                    handleViewTask(request, response, pathInfo, currentUser);
                    break;
                    
                case "/task/edit":
                    handleEditTaskGet(request, response, pathInfo, currentUser);
                    break;
                    
                case "/task/status":
                    handleStatusGet(request, response, pathInfo, currentUser);
                    break;
                    
                case "/task/delete":
                    handleDeleteTask(request, response, pathInfo, currentUser);
                    break;
                    
                default:
                    response.sendRedirect(request.getContextPath() + "/tasks");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void handleListTasks(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        // Get filter parameters
        String statusFilter = request.getParameter("status");
        String projectFilter = request.getParameter("project");
        
        // Get tasks based on user role and filters
        List<Task> tasks = new ArrayList<>();
        if (currentUser.getRole().equalsIgnoreCase("admin")) {
            tasks = taskController.getAllTasks();
        } else {
            // Get tasks where user is either creator or assignee
            List<Task> assignedTasks = taskController.getTasksByAssignee(currentUser.getId());
            List<Task> createdTasks = taskController.getTasksByCreator(currentUser.getId());
            
            // Combine and remove duplicates
            tasks.addAll(assignedTasks);
            for (Task task : createdTasks) {
                if (!tasks.contains(task)) {
                    tasks.add(task);
                }
            }
        }
        
        // Apply filters
        if (statusFilter != null && !statusFilter.isEmpty()) {
            tasks.removeIf(task -> !task.getStatus().equals(statusFilter));
        }
        
        if (projectFilter != null && !projectFilter.isEmpty()) {
            try {
                int projectId = Integer.parseInt(projectFilter);
                tasks.removeIf(task -> task.getProjectId() != projectId);
            } catch (NumberFormatException e) {
                // Invalid project ID, ignore filter
            }
        }
        
        // Get projects for filter dropdown
        List<Project> projects = projectController.getAllProjects();
        
        request.setAttribute("tasks", tasks);
        request.setAttribute("projects", projects);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("projectFilter", projectFilter);
        
        request.getRequestDispatcher("/WEB-INF/views/task/list.jsp").forward(request, response);
    }
    
    private void handleViewTask(HttpServletRequest request, HttpServletResponse response, String pathInfo, User currentUser) 
            throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int taskId = Integer.parseInt(pathInfo.substring(1));
                Task task = taskController.getTaskById(taskId);
                
                if (task != null) {
                    boolean canView = currentUser.getRole().equalsIgnoreCase("admin") || 
                                    task.getCreatedBy() == currentUser.getId() || 
                                    task.getAssignedTo() == currentUser.getId();
                    
                    if (canView) {
                        List<TaskLog> logs = taskController.getTaskLogs(taskId);
                        request.setAttribute("task", task);
                        request.setAttribute("logs", logs);
                        request.getRequestDispatcher("/WEB-INF/views/task/view.jsp").forward(request, response);
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                // Invalid task ID
            }
        }
        response.sendRedirect(request.getContextPath() + "/tasks?error=unauthorized");
    }
    
    private void handleEditTaskGet(HttpServletRequest request, HttpServletResponse response, String pathInfo, User currentUser) 
            throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int taskId = Integer.parseInt(pathInfo.substring(1));
                Task task = taskController.getTaskById(taskId);
                
                if (task != null) {
                    boolean canEdit = currentUser.getRole().equalsIgnoreCase("admin") || 
                                    task.getCreatedBy() == currentUser.getId() || 
                                    task.getAssignedTo() == currentUser.getId();
                    
                    if (canEdit) {
                        prepareTaskForm(request);
                        request.setAttribute("task", task);
                        request.getRequestDispatcher("/WEB-INF/views/task/edit.jsp").forward(request, response);
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                // Invalid task ID
            }
        }
        response.sendRedirect(request.getContextPath() + "/tasks?error=unauthorized");
    }
    
    private void handleStatusGet(HttpServletRequest request, HttpServletResponse response, String pathInfo, User currentUser) 
            throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int taskId = Integer.parseInt(pathInfo.substring(1));
                Task task = taskController.getTaskById(taskId);
                
                if (task != null) {
                    boolean canUpdateStatus = currentUser.getRole().equalsIgnoreCase("admin") || 
                                           task.getCreatedBy() == currentUser.getId() || 
                                           task.getAssignedTo() == currentUser.getId();
                    
                    if (canUpdateStatus) {
                        request.setAttribute("task", task);
                        request.getRequestDispatcher("/WEB-INF/views/task/status.jsp").forward(request, response);
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                // Invalid task ID
            }
        }
        response.sendRedirect(request.getContextPath() + "/tasks?error=unauthorized");
    }
    
    private void prepareTaskForm(HttpServletRequest request) {
        List<User> users = userController.getAllUsers();
        List<Project> projects = projectController.getAllProjects();
        request.setAttribute("users", users);
        request.setAttribute("projects", projects);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        try {
            switch (path) {
                case "/task/create":
                    handleCreateTask(request, response, currentUser);
                    break;
                    
                case "/task/edit":
                    handleEditTaskPost(request, response, pathInfo, currentUser);
                    break;
                    
                case "/task/status":
                    handleStatusPost(request, response, pathInfo, currentUser);
                    break;
                    
                default:
                    response.sendRedirect(request.getContextPath() + "/tasks");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void handleCreateTask(HttpServletRequest request, HttpServletResponse response, User currentUser) 
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String dueDateStr = request.getParameter("dueDate");
        String assignedToStr = request.getParameter("assignedTo");
        String projectIdStr = request.getParameter("projectId");
        
        try {
            Date dueDate = Date.valueOf(dueDateStr);
            int assignedTo = Integer.parseInt(assignedToStr);
            Integer projectId = null;
            if (projectIdStr != null && !projectIdStr.trim().isEmpty()) {
                projectId = Integer.parseInt(projectIdStr);
            }
            
            Map<String, String> errors = taskController.createTask(
                title, description, dueDate, assignedTo, projectId, currentUser.getId()
            );
            
            if (errors.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/tasks?success=created");
                return;
            } else {
                request.setAttribute("errors", errors);
                request.setAttribute("task", new Task());
                prepareTaskForm(request);
                request.getRequestDispatcher("/WEB-INF/views/task/create.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Invalid input: " + e.getMessage());
            prepareTaskForm(request);
            request.getRequestDispatcher("/WEB-INF/views/task/create.jsp").forward(request, response);
        }
    }
    
    private void handleEditTaskPost(HttpServletRequest request, HttpServletResponse response, String pathInfo, User currentUser) 
            throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int taskId = Integer.parseInt(pathInfo.substring(1));
                Task existingTask = taskController.getTaskById(taskId);
                
                if (existingTask != null) {
                    boolean canEdit = currentUser.getRole().equalsIgnoreCase("admin") || 
                                    existingTask.getCreatedBy() == currentUser.getId() || 
                                    existingTask.getAssignedTo() == currentUser.getId();
                    
                    if (canEdit) {
                        Task task = new Task();
                        task.setId(taskId);
                        task.setTitle(request.getParameter("title"));
                        task.setDescription(request.getParameter("description"));
                        task.setDueDate(Date.valueOf(request.getParameter("dueDate")));
                        task.setStatus(request.getParameter("status"));
                        task.setAssignedTo(Integer.parseInt(request.getParameter("assignedTo")));
                        
                        String projectIdStr = request.getParameter("projectId");
                        if (projectIdStr != null && !projectIdStr.trim().isEmpty()) {
                            task.setProjectId(Integer.parseInt(projectIdStr));
                        }
                        
                        Map<String, String> errors = taskController.updateTask(task, currentUser.getId());
                        
                        if (errors.isEmpty()) {
                            response.sendRedirect(request.getContextPath() + "/tasks?success=updated");
                            return;
                        } else {
                            request.setAttribute("errors", errors);
                            request.setAttribute("task", task);
                            prepareTaskForm(request);
                            request.getRequestDispatcher("/WEB-INF/views/task/edit.jsp").forward(request, response);
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Invalid input: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/tasks?error=invalid");
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/tasks?error=unauthorized");
    }
    
    private void handleStatusPost(HttpServletRequest request, HttpServletResponse response, String pathInfo, User currentUser) 
            throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int taskId = Integer.parseInt(pathInfo.substring(1));
                Task task = taskController.getTaskById(taskId);
                
                if (task != null) {
                    boolean canUpdateStatus = currentUser.getRole().equalsIgnoreCase("admin") || 
                                           task.getCreatedBy() == currentUser.getId() || 
                                           task.getAssignedTo() == currentUser.getId();
                    
                    if (canUpdateStatus) {
                        String newStatus = request.getParameter("status");
                        Map<String, String> errors = taskController.updateTaskStatus(taskId, newStatus, currentUser.getId());
                        
                        if (errors.isEmpty()) {
                            response.sendRedirect(request.getContextPath() + "/task/view/" + taskId + "?success=status");
                            return;
                        } else {
                            request.setAttribute("errors", errors);
                            request.setAttribute("task", task);
                            request.getRequestDispatcher("/WEB-INF/views/task/status.jsp").forward(request, response);
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Error updating task status: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/tasks?error=status");
    }
    
    private void handleDeleteTask(HttpServletRequest request, HttpServletResponse response, String pathInfo, User currentUser) 
            throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int taskId = Integer.parseInt(pathInfo.substring(1));
                Task task = taskController.getTaskById(taskId);
                
                if (task != null) {
                    boolean canDelete = currentUser.getRole().equalsIgnoreCase("admin") || task.getCreatedBy() == currentUser.getId();
                    
                    if (canDelete) {
                        boolean success = taskController.deleteTask(taskId);
                        if (success) {
                            response.sendRedirect(request.getContextPath() + "/tasks?success=deleted");
                            return;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                // Invalid task ID
            }
        }
        response.sendRedirect(request.getContextPath() + "/tasks?error=delete");
    }
}
