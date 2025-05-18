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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        try {
            // Get tasks assigned to current user
            List<Task> assignedTasks = taskController.getTasksByAssignee(currentUser.getId());
            request.setAttribute("assignedTasks", assignedTasks);
            
            // Get tasks created by current user
            List<Task> createdTasks = taskController.getTasksByCreator(currentUser.getId());
            request.setAttribute("createdTasks", createdTasks);
            
            // Calculate task statistics
            Map<String, Integer> taskStats = calculateTaskStats(assignedTasks);
            request.setAttribute("taskStats", taskStats);
            
            // If user is admin, get all projects
            if ("admin".equalsIgnoreCase(currentUser.getRole())) {
                List<Project> projects = projectController.getAllProjects();
                request.setAttribute("projects", projects);
            }
            
            // Handle project filter if specified
            String projectId = request.getParameter("project");
            if (projectId != null && !projectId.isEmpty()) {
                try {
                    int pid = Integer.parseInt(projectId);
                    if (assignedTasks != null) {
                        assignedTasks.removeIf(task -> task.getProjectId() != pid);
                        request.setAttribute("assignedTasks", assignedTasks);
                    }
                    if (createdTasks != null) {
                        createdTasks.removeIf(task -> task.getProjectId() != pid);
                        request.setAttribute("createdTasks", createdTasks);
                    }
                    request.setAttribute("projectFilter", pid);
                } catch (NumberFormatException e) {
                    // Invalid project ID, ignore filter
                }
            }
            
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while loading the dashboard: " + e.getMessage());
        }
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
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        // Handle post actions here
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
    
    /**
     * Calculate task statistics for the dashboard.
     */
    private Map<String, Integer> calculateTaskStats(List<Task> tasks) {
        Map<String, Integer> stats = new HashMap<>();
        int totalTasks = 0;
        int todoTasks = 0;
        int inProgressTasks = 0;
        int completedTasks = 0;
        
        if (tasks != null) {
            totalTasks = tasks.size();
            for (Task task : tasks) {
                switch (task.getStatus()) {
                    case "Pending":
                        todoTasks++;
                        break;
                    case "In Progress":
                        inProgressTasks++;
                        break;
                    case "Completed":
                        completedTasks++;
                        break;
                }
            }
        }
        
        stats.put("totalTasks", totalTasks);
        stats.put("todoTasks", todoTasks);
        stats.put("inProgressTasks", inProgressTasks);
        stats.put("completedTasks", completedTasks);
        
        return stats;
    }
}
