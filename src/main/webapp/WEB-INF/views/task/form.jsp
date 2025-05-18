<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>${task.id != null ? 'Edit Task' : 'Add New Task'}</h1>
        <a href="${pageContext.request.contextPath}/task" class="btn btn-secondary">Back to Tasks</a>
    </div>
    
    <div class="card">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/task" method="post">
                <input type="hidden" name="action" value="${task.id != null ? 'update' : 'add'}">
                <c:if test="${task.id != null}">
                    <input type="hidden" name="id" value="${task.id}">
                </c:if>
                
                <div class="form-group">
                    <label for="title" class="form-label">Title</label>
                    <input type="text" id="title" name="title" class="form-control" value="${task.title}" required>
                </div>
                
                <div class="form-group">
                    <label for="description" class="form-label">Description</label>
                    <textarea id="description" name="description" class="form-control" rows="4" required>${task.description}</textarea>
                </div>
                
                <div class="form-group">
                    <label for="projectId" class="form-label">Project</label>
                    <select id="projectId" name="projectId" class="form-select" required>
                        <option value="">Select Project</option>
                        <c:forEach var="project" items="${projects}">
                            <option value="${project.id}" ${task.project.id == project.id ? 'selected' : ''}>${project.name}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="status" class="form-label">Status</label>
                    <select id="status" name="status" class="form-select" required>
                        <option value="Pending" ${task.status == 'Pending' || task.status == null ? 'selected' : ''}>Pending</option>
                        <option value="In Progress" ${task.status == 'In Progress' ? 'selected' : ''}>In Progress</option>
                        <option value="Completed" ${task.status == 'Completed' ? 'selected' : ''}>Completed</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="priority" class="form-label">Priority</label>
                    <select id="priority" name="priority" class="form-select" required>
                        <option value="Low" ${task.priority == 'Low' || task.priority == null ? 'selected' : ''}>Low</option>
                        <option value="Medium" ${task.priority == 'Medium' ? 'selected' : ''}>Medium</option>
                        <option value="High" ${task.priority == 'High' ? 'selected' : ''}>High</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="dueDate" class="form-label">Due Date</label>
                    <input type="date" id="dueDate" name="dueDate" class="form-control" 
                           value="<fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" />" required>
                </div>
                
                <c:if test="${task.id != null}">
                    <div class="form-group">
                        <label for="comment" class="form-label">Update Comment (Optional)</label>
                        <textarea id="comment" name="comment" class="form-control" rows="2"></textarea>
                    </div>
                </c:if>
                
                <div class="d-flex justify-content-end mt-4">
                    <button type="submit" class="btn btn-primary">${task.id != null ? 'Update Task' : 'Create Task'}</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
