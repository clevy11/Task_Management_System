package com.clb.task_management_system.dao;

import com.clb.task_management_system.model.Project;
import com.clb.task_management_system.model.Task;
import com.clb.task_management_system.model.User;
import com.clb.task_management_system.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    
    private UserDAO userDAO = new UserDAO();
    private ProjectDAO projectDAO = new ProjectDAO();
    
    public Task getTaskById(int id) {
        String query = "SELECT * FROM TASKS WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Task task = mapResultSetToTask(rs);
                    loadRelatedEntities(task);
                    return task;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM TASKS ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Task task = mapResultSetToTask(rs);
                loadRelatedEntities(task);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    public List<Task> getTasksByAssignee(int assigneeId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM TASKS WHERE assigned_to = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, assigneeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = mapResultSetToTask(rs);
                    loadRelatedEntities(task);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    public List<Task> getTasksByCreator(int creatorId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM TASKS WHERE created_by = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, creatorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = mapResultSetToTask(rs);
                    loadRelatedEntities(task);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    public List<Task> getTasksByProject(int projectId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM TASKS WHERE project_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, projectId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = mapResultSetToTask(rs);
                    loadRelatedEntities(task);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    public int createTask(Task task) {
        String query = "INSERT INTO TASKS (title, description, due_date, status, assigned_to, project_id, created_by, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setDate(3, task.getDueDate());
            stmt.setString(4, task.getStatus());
            stmt.setInt(5, task.getAssignedTo());
            
            if (task.getProjectId() > 0) {
                stmt.setInt(6, task.getProjectId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setInt(7, task.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    public boolean updateTask(Task task) {
        String query = "UPDATE TASKS SET title = ?, description = ?, due_date = ?, status = ?, assigned_to = ?, " +
                "project_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setDate(3, task.getDueDate());
            stmt.setString(4, task.getStatus());
            stmt.setInt(5, task.getAssignedTo());
            
            if (task.getProjectId() > 0) {
                stmt.setInt(6, task.getProjectId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setInt(7, task.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteTask(int taskId) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // First delete related task logs to avoid foreign key constraint violation
            String deleteLogsQuery = "DELETE FROM task_logs WHERE task_id = ?";
            try (PreparedStatement logStmt = conn.prepareStatement(deleteLogsQuery)) {
                logStmt.setInt(1, taskId);
                logStmt.executeUpdate();
            }
            
            // Then delete the task
            String deleteTaskQuery = "DELETE FROM tasks WHERE id = ?";
            try (PreparedStatement taskStmt = conn.prepareStatement(deleteTaskQuery)) {
                taskStmt.setInt(1, taskId);
                int result = taskStmt.executeUpdate();
                
                conn.commit();
                return result > 0;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
    
    public void loadRelatedEntities(Task task) {
        if (task == null) return;
        
        try {
            // Load assignee
            if (task.getAssignedTo() > 0) {
                User assignee = userDAO.getUserById(task.getAssignedTo());
                task.setAssignee(assignee);
            }
            
            // Load creator
            if (task.getCreatedBy() > 0) {
                User creator = userDAO.getUserById(task.getCreatedBy());
                task.setCreator(creator);
            }
            
            // Load project
            if (task.getProjectId() > 0) {
                Project project = projectDAO.getProjectById(task.getProjectId());
                task.setProject(project);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setDueDate(rs.getDate("due_date"));
        task.setStatus(rs.getString("status"));
        task.setAssignedTo(rs.getInt("assigned_to"));
        task.setProjectId(rs.getInt("project_id"));
        task.setCreatedBy(rs.getInt("created_by"));
        task.setCreatedAt(rs.getTimestamp("created_at"));
        task.setUpdatedAt(rs.getTimestamp("updated_at"));
        return task;
    }
}
