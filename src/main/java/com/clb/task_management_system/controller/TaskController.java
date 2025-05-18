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
    /**
     * Valid task statuses
     */
    public static final String STATUS_TODO = "Pending";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_DONE = "Completed";
    
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
            task.setStatus(STATUS_TODO); // Default status aligned with database ENUM
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
                log.setOldStatus(null);
                log.setNewStatus(STATUS_TODO);
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
            // Get the current task to check for status change
            Task currentTask = taskDAO.getTaskById(task.getId());
            if (currentTask == null) {
                errors.put("general", "Task not found");
                return errors;
            }
            
            // Validate status
            String status = task.getStatus();
            if (!isValidStatus(status)) {
                errors.put("status", "Invalid status value");
                return errors;
            }
            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            task.setUpdatedAt(now);
            
            // Update the task
            boolean success = taskDAO.updateTask(task);
            
            if (!success) {
                errors.put("general", "Failed to update task");
                return errors;
            }
            
            // Create task log if status changed
            if (!currentTask.getStatus().equals(task.getStatus())) {
                TaskLog log = new TaskLog();
                log.setTaskId(task.getId());
                log.setOldStatus(currentTask.getStatus());
                log.setNewStatus(task.getStatus());
                log.setChangedAt(now);
                log.setChangedBy(changedBy);
                
                boolean logSuccess = taskLogDAO.createTaskLog(log);
                if (!logSuccess) {
                    errors.put("warning", "Task updated but failed to create status change log");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.put("general", "An error occurred while updating the task: " + e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * Updates a task's status.
     */
    public Map<String, String> updateTaskStatus(int taskId, String newStatus, int changedBy) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate status
        if (!isValidStatus(newStatus)) {
            errors.put("status", "Invalid status value");
            return errors;
        }
        
        try {
            Task task = taskDAO.getTaskById(taskId);
            if (task == null) {
                errors.put("general", "Task not found");
                return errors;
            }
            
            String oldStatus = task.getStatus();
            task.setStatus(newStatus);
            task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            boolean success = taskDAO.updateTask(task);
            
            if (success) {
                // Create task log
                TaskLog log = new TaskLog();
                log.setTaskId(taskId);
                log.setOldStatus(oldStatus);
                log.setNewStatus(newStatus);
                log.setChangedAt(new Timestamp(System.currentTimeMillis()));
                log.setChangedBy(changedBy);
                
                boolean logSuccess = taskLogDAO.createTaskLog(log);
                if (!logSuccess) {
                    errors.put("warning", "Status updated but failed to create status change log");
                }
            } else {
                errors.put("general", "Failed to update task status");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.put("general", "An error occurred while updating the task status: " + e.getMessage());
        }
        
        return errors;
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
     * Gets all logs for a specific task.
     */
    public List<TaskLog> getTaskLogs(int taskId) {
        try {
            return taskLogDAO.getLogsByTaskId(taskId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Validates task input data.
     */
    private Map<String, String> validateTaskInput(String title, String description, Date dueDate) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate title
        if (title == null || title.trim().isEmpty()) {
            errors.put("title", "Task title is required");
        } else if (title.length() < 2 || title.length() > 100) {
            errors.put("title", "Task title must be between 2 and 100 characters");
        }
        
        // Validate description length if provided
        if (description != null && description.length() > 500) {
            errors.put("description", "Task description cannot exceed 500 characters");
        }
        
        // Validate due date
        if (dueDate == null) {
            errors.put("dueDate", "Due date is required");
        }
        
        return errors;
    }
    
    /**
     * Checks if the provided status is valid.
     */
    private boolean isValidStatus(String status) {
        return STATUS_TODO.equals(status) || STATUS_IN_PROGRESS.equals(status) || STATUS_DONE.equals(status);
    }
}
