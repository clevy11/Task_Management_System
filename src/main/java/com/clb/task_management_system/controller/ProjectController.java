package com.clb.task_management_system.controller;

import com.clb.task_management_system.dao.ProjectDAO;
import com.clb.task_management_system.model.Project;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for Project-related operations.
 * Part of the Controller component in MVC architecture.
 * Acts as an intermediary between the View (Servlets/JSP) and the Model (DAO/Model classes).
 */
public class ProjectController {
    private ProjectDAO projectDAO;
    
    public ProjectController() {
        this.projectDAO = new ProjectDAO();
    }
    
    /**
     * Creates a new project with the provided information.
     * 
     * @param name The project name
     * @param description The project description
     * @param startDate The project start date
     * @param endDate The project end date
     * @param createdBy The ID of the user creating the project
     * @return A map of validation errors (empty if creation is successful)
     */
    public Map<String, String> createProject(String name, String description, Date startDate, Date endDate, int createdBy) {
        Map<String, String> errors = validateProjectInput(name, description, startDate, endDate);
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        try {
            Project project = new Project();
            project.setName(name);
            project.setDescription(description);
            project.setStartDate(startDate);
            project.setEndDate(endDate);
            project.setCreatedBy(createdBy);
            
            boolean success = projectDAO.createProject(project);
            if (!success) {
                errors.put("general", "Failed to create project");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.put("general", "An error occurred during project creation");
        }
        
        return errors;
    }
    
    /**
     * Gets a project by its ID.
     * 
     * @param projectId The project's ID
     * @return The Project object if found, null otherwise
     */
    public Project getProjectById(int projectId) {
        try {
            return projectDAO.getProjectById(projectId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets all projects in the system.
     * 
     * @return A list of all projects
     */
    public List<Project> getAllProjects() {
        try {
            return projectDAO.getAllProjects();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets all projects created by a specific user.
     * 
     * @param userId The ID of the user
     * @return A list of projects created by the user
     */
    public List<Project> getProjectsByCreator(int userId) {
        try {
            return projectDAO.getProjectsByCreator(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Updates a project's information.
     * 
     * @param project The Project object to update
     * @return A map of validation errors (empty if update is successful)
     */
    public Map<String, String> updateProject(Project project) {
        Map<String, String> errors = validateProjectInput(
            project.getName(), 
            project.getDescription(), 
            project.getStartDate(), 
            project.getEndDate()
        );
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        try {
            boolean success = projectDAO.updateProject(project);
            if (!success) {
                errors.put("general", "Failed to update project");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.put("general", "An error occurred during project update");
        }
        
        return errors;
    }
    
    /**
     * Deletes a project by its ID.
     * 
     * @param projectId The ID of the project to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteProject(int projectId) {
        try {
            return projectDAO.deleteProject(projectId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Validates project input.
     * 
     * @param name The project name
     * @param description The project description
     * @param startDate The project start date
     * @param endDate The project end date
     * @return A map of validation errors (empty if all inputs are valid)
     */
    private Map<String, String> validateProjectInput(String name, String description, Date startDate, Date endDate) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Project name is required");
        } else if (name.length() < 2 || name.length() > 100) {
            errors.put("name", "Project name must be between 2 and 100 characters");
        }
        
        // Validate dates
        if (startDate != null && endDate != null && startDate.after(endDate)) {
            errors.put("endDate", "End date must be after start date");
        }
        
        return errors;
    }
}
