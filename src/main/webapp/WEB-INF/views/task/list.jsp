<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Tasks</h1>
        <a href="${pageContext.request.contextPath}/task?action=showAddForm" class="btn btn-primary">Add New Task</a>
    </div>
    
    <div class="card mb-4">
        <div class="card-header">
            <h2 style="margin: 0; font-size: 1.25rem;">Filter Tasks</h2>
        </div>
        <div class="card-body">
            <div class="d-flex">
                <div class="form-group mr-3" style="min-width: 200px;">
                    <label for="statusFilter" class="form-label">Status</label>
                    <select id="statusFilter" class="form-select">
                        <option value="">All Statuses</option>
                        <option value="Pending" ${param.status == 'Pending' ? 'selected' : ''}>Pending</option>
                        <option value="In Progress" ${param.status == 'In Progress' ? 'selected' : ''}>In Progress</option>
                        <option value="Completed" ${param.status == 'Completed' ? 'selected' : ''}>Completed</option>
                    </select>
                </div>
                
                <div class="form-group" style="min-width: 200px;">
                    <label for="projectFilter" class="form-label">Project</label>
                    <select id="projectFilter" class="form-select">
                        <option value="">All Projects</option>
                        <c:forEach var="project" items="${projects}">
                            <option value="${project.id}" ${param.project == project.id ? 'selected' : ''}>${project.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </div>
    </div>
    
    <div class="card">
        <div class="card-body">
            <c:if test="${empty tasks}">
                <p class="text-center">No tasks found matching your criteria.</p>
            </c:if>
            
            <c:if test="${not empty tasks}">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Project</th>
                                <th>Status</th>
                                <th>Priority</th>
                                <th>Due Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="task" items="${tasks}">
                                <tr>
                                    <td>${task.title}</td>
                                    <td>${task.project.name}</td>
                                    <td>
                                        <span class="badge" data-status="${task.status}">${task.status}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${task.priority == 'High'}">
                                                <span class="badge badge-danger">High</span>
                                            </c:when>
                                            <c:when test="${task.priority == 'Medium'}">
                                                <span class="badge badge-warning">Medium</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-info">Low</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${task.dueDate}" pattern="MMM dd, yyyy" />
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/task?action=view&id=${task.id}" class="btn btn-info btn-sm">View</a>
                                        <a href="${pageContext.request.contextPath}/task?action=showEditForm&id=${task.id}" class="btn btn-secondary btn-sm">Edit</a>
                                        <a href="${pageContext.request.contextPath}/task?action=delete&id=${task.id}" 
                                           class="btn btn-danger btn-sm"
                                           onclick="confirmDelete(event, 'task')">Delete</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
