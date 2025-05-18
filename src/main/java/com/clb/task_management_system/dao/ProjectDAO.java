package com.clb.task_management_system.dao;

import com.clb.task_management_system.model.Project;
import com.clb.task_management_system.model.User;
import com.clb.task_management_system.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    
    private UserDAO userDAO = new UserDAO();
    
    public Project getProjectById(int id) {
        String query = "SELECT * FROM PROJECTS WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Project project = mapResultSetToProject(rs);
                    // Get the creator user
                    User creator = userDAO.getUserById(project.getCreatedBy());
                    project.setCreator(creator);
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
        String query = "SELECT * FROM PROJECTS ORDER BY name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Project project = mapResultSetToProject(rs);
                // Get the creator user
                User creator = userDAO.getUserById(project.getCreatedBy());
                project.setCreator(creator);
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return projects;
    }
    
    public List<Project> getProjectsByCreator(int creatorId) {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT * FROM PROJECTS WHERE created_by = ? ORDER BY name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, creatorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Project project = mapResultSetToProject(rs);
                    // Get the creator user
                    User creator = userDAO.getUserById(project.getCreatedBy());
                    project.setCreator(creator);
                    projects.add(project);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return projects;
    }
    
    public boolean createProject(Project project) {
        String query = "INSERT INTO PROJECTS (name, description, start_date, end_date, created_by) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, project.getStartDate());
            stmt.setDate(4, project.getEndDate());
            stmt.setInt(5, project.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        project.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean updateProject(Project project) {
        String query = "UPDATE PROJECTS SET name = ?, description = ?, start_date = ?, end_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, project.getStartDate());
            stmt.setDate(4, project.getEndDate());
            stmt.setInt(5, project.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean deleteProject(int id) {
        String query = "DELETE FROM PROJECTS WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
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
}
