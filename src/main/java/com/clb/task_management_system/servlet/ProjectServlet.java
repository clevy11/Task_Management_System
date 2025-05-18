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

@WebServlet(name = "projectServlet", urlPatterns = {"/projects", "/project/create", "/project/edit/*", "/project/delete/*", "/project/view/*"})
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
        
        try {
            // Get current user
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("user");
            
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
                return;
            }
            
            switch (path) {
                case "/projects":
                    handleListProjects(request, response);
                    break;
                    
                case "/project/create":
                    handleShowCreateForm(request, response, currentUser);
                    break;
                    
                case "/project/edit":
                    handleShowEditForm(request, response, currentUser, pathInfo);
                    break;
                    
                case "/project/view":
                    handleViewProject(request, response, pathInfo);
                    break;
                    
                default:
                    response.sendRedirect(request.getContextPath() + "/projects");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        
        try {
            // Get current user
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("user");
            
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth?action=showLogin");
                return;
            }
            
            switch (path) {
                case "/project/create":
                    handleCreateProject(request, response, currentUser);
                    break;
                    
                case "/project/edit":
                    if (pathInfo != null && pathInfo.length() > 1) {
                        int projectId = Integer.parseInt(pathInfo.substring(1));
                        handleUpdateProject(request, response, projectId);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/projects?error=invalid");
                    }
                    break;
                    
                default:
                    response.sendRedirect(request.getContextPath() + "/projects");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void handleListProjects(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Project> projects = projectController.getAllProjects();
        request.setAttribute("projects", projects);
        request.getRequestDispatcher("/WEB-INF/views/project/list.jsp").forward(request, response);
    }
    
    private void handleShowCreateForm(HttpServletRequest request, HttpServletResponse response, User currentUser) throws ServletException, IOException {
        if (!currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/projects?error=unauthorized");
            return;
        }
        
        List<User> users = userController.getAllUsers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/views/project/create.jsp").forward(request, response);
    }
    
    private void handleShowEditForm(HttpServletRequest request, HttpServletResponse response, User currentUser, String pathInfo) throws ServletException, IOException {
        if (!currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/projects?error=unauthorized");
            return;
        }
        
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int projectId = Integer.parseInt(pathInfo.substring(1));
                Project project = projectController.getProjectById(projectId);
                
                if (project != null) {
                    List<User> users = userController.getAllUsers();
                    request.setAttribute("users", users);
                    request.setAttribute("project", project);
                    request.getRequestDispatcher("/WEB-INF/views/project/edit.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid project ID
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/projects?error=invalid");
    }
    
    private void handleViewProject(HttpServletRequest request, HttpServletResponse response, String pathInfo) throws ServletException, IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int projectId = Integer.parseInt(pathInfo.substring(1));
                Project project = projectController.getProjectById(projectId);
                
                if (project != null) {
                    // Get tasks for this project
                    List<Task> tasks = taskController.getTasksByProject(projectId);
                    
                    request.setAttribute("project", project);
                    request.setAttribute("projectTasks", tasks);
                    request.getRequestDispatcher("/WEB-INF/views/project/view.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid project ID
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/projects?error=invalid");
    }
    
    private void handleCreateProject(HttpServletRequest request, HttpServletResponse response, User currentUser) throws ServletException, IOException {
        if (!currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/projects?error=unauthorized");
            return;
        }
        
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        try {
            Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr) : null;
            Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : null;
            
            Map<String, String> errors = projectController.createProject(name, description, startDate, endDate, currentUser.getId());
            
            if (errors.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/projects?success=created");
            } else {
                request.setAttribute("errors", errors);
                request.setAttribute("project", new Project(0, name, description, startDate, endDate, currentUser.getId()));
                List<User> users = userController.getAllUsers();
                request.setAttribute("users", users);
                request.getRequestDispatcher("/WEB-INF/views/project/create.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid date format");
            List<User> users = userController.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/project/create.jsp").forward(request, response);
        }
    }
    
    private void handleUpdateProject(HttpServletRequest request, HttpServletResponse response, int projectId) throws ServletException, IOException {
        Project project = projectController.getProjectById(projectId);
        
        if (project == null) {
            response.sendRedirect(request.getContextPath() + "/projects?error=invalid");
            return;
        }
        
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        try {
            if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                project.setStartDate(Date.valueOf(startDateStr));
            }
            
            if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                project.setEndDate(Date.valueOf(endDateStr));
            }
            
            project.setName(name);
            project.setDescription(description);
            
            Map<String, String> errors = projectController.updateProject(project);
            
            if (errors.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/project/view/" + projectId + "?success=updated");
            } else {
                request.setAttribute("errors", errors);
                request.setAttribute("project", project);
                List<User> users = userController.getAllUsers();
                request.setAttribute("users", users);
                request.getRequestDispatcher("/WEB-INF/views/project/edit.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid date format");
            request.setAttribute("project", project);
            List<User> users = userController.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/views/project/edit.jsp").forward(request, response);
        }
    }
}
