package com.clb.task_management_system.servlet;

import com.clb.task_management_system.controller.ProjectController;
import com.clb.task_management_system.controller.TaskController;
import com.clb.task_management_system.controller.UserController;
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
import java.sql.Date;
import java.util.List;
import java.util.Map;

@WebServlet(name = "projectServlet", urlPatterns = {"/project", "/project/create", "/project/edit/*", "/project/delete/*", "/project/view/*"})
public class ProjectServlet extends HttpServlet {
    
    private ProjectController projectController;
    private TaskController taskController;
    private UserController userController;
    
    @Override
    public void init() throws ServletException {
        projectController = new ProjectController();
        taskController = new TaskController();
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
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        // Check if user is admin
        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        try {
            if ("/project".equals(path)) {
                handleListProjects(request, response);
            } else if ("/project/create".equals(path)) {
                handleShowCreateForm(request, response);
            } else if ("/project/edit".equals(path)) {
                handleShowEditForm(request, response, pathInfo);
            } else if ("/project/view".equals(path)) {
                handleViewProject(request, response, pathInfo);
            } else if ("/project/delete".equals(path)) {
                handleDeleteProject(request, response, pathInfo);
            } else {
                response.sendRedirect(request.getContextPath() + "/project");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/project");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get current user
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        // Check if user is admin
        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        try {
            String path = request.getServletPath();
            String pathInfo = request.getPathInfo();
            
            if ("/project/create".equals(path)) {
                handleCreateProject(request, response, currentUser);
            } else if ("/project/edit".equals(path)) {
                handleUpdateProject(request, response, currentUser, pathInfo);
            } else {
                response.sendRedirect(request.getContextPath() + "/project");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/project");
        }
    }
    
    private void handleListProjects(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Project> projects = projectController.getAllProjects();
        request.setAttribute("projects", projects);
        request.getRequestDispatcher("/WEB-INF/views/project/list.jsp").forward(request, response);
    }
    
    private void handleShowCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/project/form.jsp").forward(request, response);
    }
    
    private void handleShowEditForm(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int projectId = Integer.parseInt(pathInfo.substring(1));
                Project project = projectController.getProjectById(projectId);
                
                if (project != null) {
                    request.setAttribute("project", project);
                    request.getRequestDispatcher("/WEB-INF/views/project/form.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid project ID
            }
        }
        response.sendRedirect(request.getContextPath() + "/project");
    }
    
    private void handleViewProject(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int projectId = Integer.parseInt(pathInfo.substring(1));
                Project project = projectController.getProjectById(projectId);
                
                if (project != null) {
                    List<Task> projectTasks = taskController.getTasksByProject(projectId);
                    request.setAttribute("project", project);
                    request.setAttribute("tasks", projectTasks);
                    request.getRequestDispatcher("/WEB-INF/views/project/view.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid project ID
            }
        }
        response.sendRedirect(request.getContextPath() + "/project");
    }
    
    private void handleCreateProject(HttpServletRequest request, HttpServletResponse response, User currentUser) throws ServletException, IOException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        try {
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);
            
            Map<String, String> errors = projectController.createProject(
                name,
                description,
                startDate,
                endDate,
                currentUser.getId()
            );
            
            if (errors.isEmpty()) {
                request.getSession().setAttribute("successMessage", "Project created successfully");
                response.sendRedirect(request.getContextPath() + "/project");
            } else {
                Project project = new Project();
                project.setName(name);
                project.setDescription(description);
                project.setStartDate(startDate);
                project.setEndDate(endDate);
                
                request.setAttribute("errors", errors);
                request.setAttribute("project", project);
                request.getRequestDispatcher("/WEB-INF/views/project/form.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid input data");
            request.getRequestDispatcher("/WEB-INF/views/project/form.jsp").forward(request, response);
        }
    }
    
    private void handleUpdateProject(HttpServletRequest request, HttpServletResponse response, User currentUser, String pathInfo) throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int projectId = Integer.parseInt(pathInfo.substring(1));
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                
                Date startDate = Date.valueOf(startDateStr);
                Date endDate = Date.valueOf(endDateStr);
                
                Project project = new Project();
                project.setId(projectId);
                project.setName(name);
                project.setDescription(description);
                project.setStartDate(startDate);
                project.setEndDate(endDate);
                project.setCreatedBy(currentUser.getId());
                
                Map<String, String> errors = projectController.updateProject(project);
                
                if (errors.isEmpty()) {
                    request.getSession().setAttribute("successMessage", "Project updated successfully");
                    response.sendRedirect(request.getContextPath() + "/project");
                } else {
                    request.setAttribute("errors", errors);
                    request.setAttribute("project", project);
                    request.getRequestDispatcher("/WEB-INF/views/project/form.jsp").forward(request, response);
                }
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Invalid input data");
                request.getRequestDispatcher("/WEB-INF/views/project/form.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/project");
        }
    }
    
    private void handleDeleteProject(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int projectId = Integer.parseInt(pathInfo.substring(1));
                boolean success = projectController.deleteProject(projectId);
                
                if (success) {
                    request.getSession().setAttribute("successMessage", "Project deleted successfully");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to delete project");
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errorMessage", "Invalid project ID");
            }
        }
        response.sendRedirect(request.getContextPath() + "/project");
    }
}
