package com.clb.task_management_system.dao;

import com.clb.task_management_system.model.Project;
import com.clb.task_management_system.model.User;
import com.clb.task_management_system.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ProjectDAO {
    
    private UserDAO userDAO = new UserDAO();
    
    public Project getProjectById(int id) {
        String query = "SELECT p.*, COUNT(t.id) as task_count " +
                      "FROM PROJECTS p " +
                      "LEFT JOIN TASKS t ON p.id = t.project_id " +
                      "WHERE p.id = ? " +
                      "GROUP BY p.id, p.name, p.description, p.start_date, p.end_date, p.created_by";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Project project = mapResultSetToProject(rs);
                    // Get the creator user
                    User creator = userDAO.getUserById(project.getCreatedBy());
                    project.setCreator(creator);
                    project.setTaskCount(rs.getInt("task_count"));
                    return project;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT p.*, COUNT(t.id) as task_count " +
                      "FROM PROJECTS p " +
                      "LEFT JOIN TASKS t ON p.id = t.project_id " +
                      "GROUP BY p.id, p.name, p.description, p.start_date, p.end_date, p.created_by " +
                      "ORDER BY p.name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Project project = mapResultSetToProject(rs);
                // Get the creator user
                User creator = userDAO.getUserById(project.getCreatedBy());
                project.setCreator(creator);
                project.setTaskCount(rs.getInt("task_count"));
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return projects;
    }
    
    public List<Project> getProjectsByCreator(int creatorId) {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT p.*, COUNT(t.id) as task_count " +
                      "FROM PROJECTS p " +
                      "LEFT JOIN TASKS t ON p.id = t.project_id " +
                      "WHERE p.created_by = ? " +
                      "GROUP BY p.id, p.name, p.description, p.start_date, p.end_date, p.created_by " +
                      "ORDER BY p.name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, creatorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Project project = mapResultSetToProject(rs);
                    // Get the creator user
                    User creator = userDAO.getUserById(project.getCreatedBy());
                    project.setCreator(creator);
                    project.setTaskCount(rs.getInt("task_count"));
                    projects.add(project);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return projects;
    }
    
    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        project.setStartDate(rs.getDate("start_date"));
        project.setEndDate(rs.getDate("end_date"));
        project.setCreatedBy(rs.getInt("created_by"));
        return project;
    }
    
    public boolean createProject(Project project) {
        Map<String, String> errors = validateProject(project);
        if (!errors.isEmpty()) {
            return false;
        }
        
        String query = "INSERT INTO PROJECTS (name, description, start_date, end_date, created_by) " +
                      "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, project.getStartDate());
            stmt.setDate(4, project.getEndDate());
            stmt.setInt(5, project.getCreatedBy());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateProject(Project project) {
        Map<String, String> errors = validateProject(project);
        if (!errors.isEmpty()) {
            return false;
        }
        
        String query = "UPDATE PROJECTS SET name = ?, description = ?, start_date = ?, end_date = ? " +
                      "WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, project.getStartDate());
            stmt.setDate(4, project.getEndDate());
            stmt.setInt(5, project.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteProject(int projectId) {
        String query = "DELETE FROM PROJECTS WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, projectId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Map<String, String> validateProject(Project project) {
        Map<String, String> errors = new HashMap<>();
        
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            errors.put("name", "Project name is required");
        }
        
        if (project.getDescription() == null || project.getDescription().trim().isEmpty()) {
            errors.put("description", "Project description is required");
        }
        
        if (project.getStartDate() == null) {
            errors.put("startDate", "Start date is required");
        }
        
        if (project.getEndDate() == null) {
            errors.put("endDate", "End date is required");
        }
        
        if (project.getStartDate() != null && project.getEndDate() != null 
            && project.getStartDate().after(project.getEndDate())) {
            errors.put("endDate", "End date must be after start date");
        }
        
        return errors;
    }
}
