package com.clb.task_management_system.servlet;

import com.clb.task_management_system.controller.ProjectController;
import com.clb.task_management_system.controller.TaskController;
import com.clb.task_management_system.model.Project;
import com.clb.task_management_system.model.Task;
import com.clb.task_management_system.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling dashboard operations.
 * Part of the View component in MVC architecture.
 */
@WebServlet(name = "dashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    
    private TaskController taskController;
    private ProjectController projectController;
    
    @Override
    public void init() throws ServletException {
        taskController = new TaskController();
        projectController = new ProjectController();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            // User not logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        // Get tasks assigned to current user
        List<Task> assignedTasks = taskController.getTasksByAssignee(currentUser.getId());
        request.setAttribute("assignedTasks", assignedTasks);
        
        // Get all projects
        List<Project> projects = projectController.getAllProjects();
        request.setAttribute("projects", projects);
        
        // Get filter parameters
        String statusFilter = request.getParameter("status");
        String projectFilter = request.getParameter("project");
        
        // Apply filters if provided
        if (statusFilter != null && !statusFilter.isEmpty()) {
            // Filter by status - we need to filter the already fetched tasks
            // since we don't have a direct controller method for this
            if (assignedTasks != null) {
                assignedTasks.removeIf(task -> !task.getStatus().equals(statusFilter));
                request.setAttribute("assignedTasks", assignedTasks);
            }
            request.setAttribute("statusFilter", statusFilter);
        }
        
        if (projectFilter != null && !projectFilter.isEmpty()) {
            try {
                int projectId = Integer.parseInt(projectFilter);
                List<Task> projectTasks = taskController.getTasksByProject(projectId);
                
                // If we're also filtering by status, we need to apply that filter too
                if (statusFilter != null && !statusFilter.isEmpty() && projectTasks != null) {
                    projectTasks.removeIf(task -> !task.getStatus().equals(statusFilter));
                }
                
                request.setAttribute("assignedTasks", projectTasks);
                request.setAttribute("projectFilter", projectId);
            } catch (NumberFormatException e) {
                // Invalid project ID, ignore filter
            }
        }
        
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            // User not logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        switch (action) {
            case "updateTaskStatus":
                updateTaskStatus(request, response, currentUser);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
    
    private void updateTaskStatus(HttpServletRequest request, HttpServletResponse response, User currentUser) throws IOException {
        try {
            int taskId = Integer.parseInt(request.getParameter("taskId"));
            String newStatus = request.getParameter("newStatus");
            
            if (newStatus != null && !newStatus.isEmpty()) {
                boolean success = taskController.updateTaskStatus(taskId, newStatus, currentUser.getId());
                
                if (success) {
                    request.getSession().setAttribute("successMessage", "Task status updated successfully");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to update task status");
                }
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid task ID");
        }
        
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
