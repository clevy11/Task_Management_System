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
        String query = "SELECT * FROM TASKS ORDER BY due_date";
        
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
        String query = "SELECT * FROM TASKS WHERE assigned_to = ? ORDER BY due_date";
        
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
        String query = "SELECT * FROM TASKS WHERE created_by = ? ORDER BY due_date";
        
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
        String query = "SELECT * FROM TASKS WHERE project_id = ? ORDER BY due_date";
        
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
    
    public List<Task> getTasksByStatus(String status) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM TASKS WHERE status = ? ORDER BY due_date";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            
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
    
    public boolean createTask(Task task) {
        String query = "INSERT INTO TASKS (title, description, due_date, status, assigned_to, project_id, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
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
                        task.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
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
        }
        
        return false;
    }
    
    public boolean updateTaskStatus(int taskId, String newStatus, int changedBy) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Get current task
            String taskQuery = "SELECT * FROM TASKS WHERE id = ?";
            String oldStatus = null;
            
            try (PreparedStatement taskStmt = conn.prepareStatement(taskQuery)) {
                taskStmt.setInt(1, taskId);
                
                try (ResultSet rs = taskStmt.executeQuery()) {
                    if (rs.next()) {
                        oldStatus = rs.getString("status");
                    } else {
                        // Task not found
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            // Update task status
            String updateQuery = "UPDATE TASKS SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newStatus);
                updateStmt.setInt(2, taskId);
                
                int affectedRows = updateStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Create task log
            String logQuery = "INSERT INTO TASK_LOGS (task_id, old_status, new_status, changed_by) VALUES (?, ?, ?, ?)";
            try (PreparedStatement logStmt = conn.prepareStatement(logQuery)) {
                logStmt.setInt(1, taskId);
                logStmt.setString(2, oldStatus);
                logStmt.setString(3, newStatus);
                logStmt.setInt(4, changedBy);
                
                int affectedRows = logStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean deleteTask(int id) {
        String query = "DELETE FROM TASKS WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
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
    
    private void loadRelatedEntities(Task task) throws SQLException {
        // Load assignee if assigned
        if (task.getAssignedTo() > 0) {
            User assignee = userDAO.getUserById(task.getAssignedTo());
            if (assignee != null) {
                task.setAssignee(assignee);
            }
        }
        
        // Load creator
        if (task.getCreatedBy() > 0) {
            User creator = userDAO.getUserById(task.getCreatedBy());
            if (creator != null) {
                task.setCreator(creator);
            }
        }
        
        // Load project if available
        if (task.getProjectId() > 0) {
            Project project = projectDAO.getProjectById(task.getProjectId());
            if (project != null) {
                task.setProject(project);
            }
        }
    }
}
