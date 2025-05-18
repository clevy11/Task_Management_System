package com.clb.task_management_system.dao;

import com.clb.task_management_system.model.Task;
import com.clb.task_management_system.model.TaskLog;
import com.clb.task_management_system.model.User;
import com.clb.task_management_system.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskLogDAO {
    
    private UserDAO userDAO = new UserDAO();
    private TaskDAO taskDAO = new TaskDAO();
    
    public List<TaskLog> getLogsByTaskId(int taskId) {
        List<TaskLog> logs = new ArrayList<>();
        String query = "SELECT * FROM TASK_LOGS WHERE task_id = ? ORDER BY changed_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, taskId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TaskLog log = mapResultSetToTaskLog(rs);
                    
                    // Load related entities
                    User changer = userDAO.getUserById(log.getChangedBy());
                    log.setChanger(changer);
                    
                    Task task = taskDAO.getTaskById(log.getTaskId());
                    log.setTask(task);
                    
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return logs;
    }
    
    public boolean createTaskLog(TaskLog log) {
        String query = "INSERT INTO TASK_LOGS (task_id, old_status, new_status, changed_by) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, log.getTaskId());
            stmt.setString(2, log.getOldStatus());
            stmt.setString(3, log.getNewStatus());
            stmt.setInt(4, log.getChangedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        log.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Deletes all logs associated with a specific task.
     * 
     * @param taskId The ID of the task whose logs should be deleted
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteLogsByTaskId(int taskId) {
        String query = "DELETE FROM TASK_LOGS WHERE task_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, taskId);
            
            // Even if there are no logs to delete, we consider it successful
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a specific task log by its ID.
     * 
     * @param logId The ID of the log to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteTaskLog(int logId) {
        String query = "DELETE FROM TASK_LOGS WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, logId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private TaskLog mapResultSetToTaskLog(ResultSet rs) throws SQLException {
        TaskLog log = new TaskLog();
        log.setId(rs.getInt("id"));
        log.setTaskId(rs.getInt("task_id"));
        log.setOldStatus(rs.getString("old_status"));
        log.setNewStatus(rs.getString("new_status"));
        log.setChangedAt(rs.getTimestamp("changed_at"));
        log.setChangedBy(rs.getInt("changed_by"));
        return log;
    }
}
