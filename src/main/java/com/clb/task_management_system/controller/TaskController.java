package com.clb.task_management_system.controller;

import com.clb.task_management_system.dao.TaskDAO;
import com.clb.task_management_system.dao.TaskLogDAO;
import com.clb.task_management_system.model.Task;
import com.clb.task_management_system.model.TaskLog;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for Task-related operations.
 * Part of the Controller component in MVC architecture.
 * Acts as an intermediary between the View (Servlets/JSP) and the Model (DAO/Model classes).
 */
public class TaskController {
    private TaskDAO taskDAO;
    private TaskLogDAO taskLogDAO;
    
    public TaskController() {
        this.taskDAO = new TaskDAO();
        this.taskLogDAO = new TaskLogDAO();
    }
    
    /**
     * Creates a new task with the provided information.
     */
    public Map<String, String> createTask(String title, String description, Date dueDate, 
                                        int assignedTo, Integer projectId, int createdBy) {
        Map<String, String> errors = validateTaskInput(title, description, dueDate);
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        try {
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(dueDate);
            task.setStatus("TODO"); // Default status aligned with UI
            task.setAssignedTo(assignedTo);
            task.setProjectId(projectId != null ? projectId : 0);
            task.setCreatedBy(createdBy);
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            task.setCreatedAt(now);
            task.setUpdatedAt(now);
            
            int taskId = taskDAO.createTask(task);
            
            if (taskId > 0) {
                task.setId(taskId);
                // Create initial task log
                TaskLog log = new TaskLog();
                log.setTaskId(taskId);
                log.setDescription("Task created by " + createdBy);
                log.setOldStatus(null);
                log.setNewStatus("TODO");
                log.setChangedAt(now);
                log.setChangedBy(createdBy);
                
                taskLogDAO.createTaskLog(log);
            } else {
                errors.put("general", "Failed to create task");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.put("general", "An error occurred during task creation: " + e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * Gets a task by its ID.
     */
    public Task getTaskById(int taskId) {
        try {
            Task task = taskDAO.getTaskById(taskId);
            if (task != null) {
                taskDAO.loadRelatedEntities(task);
            }
            return task;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets all tasks in the system.
     */
    public List<Task> getAllTasks() {
        try {
            List<Task> tasks = taskDAO.getAllTasks();
            for (Task task : tasks) {
                taskDAO.loadRelatedEntities(task);
            }
            return tasks;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets all tasks assigned to a specific user.
     */
    public List<Task> getTasksByAssignee(int userId) {
        try {
            List<Task> tasks = taskDAO.getTasksByAssignee(userId);
            for (Task task : tasks) {
                taskDAO.loadRelatedEntities(task);
            }
            return tasks;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets all tasks created by a specific user.
     */
    public List<Task> getTasksByCreator(int userId) {
        try {
            List<Task> tasks = taskDAO.getTasksByCreator(userId);
            for (Task task : tasks) {
                taskDAO.loadRelatedEntities(task);
            }
            return tasks;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets all tasks for a specific project.
     */
    public List<Task> getTasksByProject(int projectId) {
        try {
            List<Task> tasks = taskDAO.getTasksByProject(projectId);
            for (Task task : tasks) {
                taskDAO.loadRelatedEntities(task);
            }
            return tasks;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Updates a task's information.
     */
    public Map<String, String> updateTask(Task task, int changedBy) {
        Map<String, String> errors = validateTaskInput(
            task.getTitle(), 
            task.getDescription(), 
            task.getDueDate()
        );
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        try {
            // Get the current task to check for status changes
            Task currentTask = taskDAO.getTaskById(task.getId());
            String oldStatus = currentTask != null ? currentTask.getStatus() : null;
            String newStatus = task.getStatus();
            
            // Update the task
            task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            boolean success = taskDAO.updateTask(task);
            
            if (!success) {
                errors.put("general", "Failed to update task");
                return errors;
            }
            
            // Log status change if status has changed
            if (oldStatus != null && newStatus != null && !oldStatus.equals(newStatus)) {
                TaskLog log = new TaskLog();
                log.setTaskId(task.getId());
                log.setDescription("Status changed from " + oldStatus + " to " + newStatus);
                log.setOldStatus(oldStatus);
                log.setNewStatus(newStatus);
                log.setChangedAt(new Timestamp(System.currentTimeMillis()));
                log.setChangedBy(changedBy);
                
                boolean logSuccess = taskLogDAO.createTaskLog(log);
                if (!logSuccess) {
                    errors.put("general", "Task updated but failed to log status change");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.put("general", "An error occurred during task update: " + e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * Updates a task's status.
     */
    public boolean updateTaskStatus(int taskId, String newStatus, int changedBy) {
        try {
            Task task = taskDAO.getTaskById(taskId);
            if (task == null) {
                return false;
            }
            
            String oldStatus = task.getStatus();
            task.setStatus(newStatus);
            task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            boolean success = taskDAO.updateTask(task);
            
            if (success) {
                TaskLog log = new TaskLog();
                log.setTaskId(taskId);
                log.setDescription("Status changed from " + oldStatus + " to " + newStatus);
                log.setOldStatus(oldStatus);
                log.setNewStatus(newStatus);
                log.setChangedAt(new Timestamp(System.currentTimeMillis()));
                log.setChangedBy(changedBy);
                
                taskLogDAO.createTaskLog(log);
            }
            
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a task.
     */
    public boolean deleteTask(int taskId) {
        try {
            return taskDAO.deleteTask(taskId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets task logs for a specific task.
     */
    public List<TaskLog> getTaskLogs(int taskId) {
        try {
            return taskLogDAO.getTaskLogs(taskId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private Map<String, String> validateTaskInput(String title, String description, Date dueDate) {
        Map<String, String> errors = new HashMap<>();
        
        if (title == null || title.trim().isEmpty()) {
            errors.put("title", "Title is required");
        } else if (title.length() > 100) {
            errors.put("title", "Title cannot exceed 100 characters");
        }
        
        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Description is required");
        }
        
        if (dueDate == null) {
            errors.put("dueDate", "Due date is required");
        } else {
            long currentTime = System.currentTimeMillis();
            if (dueDate.getTime() < currentTime) {
                errors.put("dueDate", "Due date cannot be in the past");
            }
        }
        
        return errors;
    }
}
