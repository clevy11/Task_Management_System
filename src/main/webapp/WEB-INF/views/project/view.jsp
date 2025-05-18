<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h2 mb-0">${project.name}</h1>
        <div>
            <a href="${pageContext.request.contextPath}/projects" class="btn btn-outline-secondary me-2">
                <i class="bi bi-arrow-left me-1"></i> Back to Projects
            </a>
            <c:if test="${sessionScope.user.id == project.createdBy || sessionScope.user.isAdmin()}">
                <a href="${pageContext.request.contextPath}/project/edit/${project.id}" class="btn btn-primary">
                    <i class="bi bi-pencil me-1"></i> Edit Project
                </a>
            </c:if>
        </div>
    </div>
    
    <div class="row">
        <div class="col-lg-8">
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-white py-3">
                    <h2 class="h5 mb-0 text-primary">Project Details</h2>
                </div>
                <div class="card-body">
                    <h3 class="h6 fw-bold mb-3">Description</h3>
                    <p class="mb-4">${project.description}</p>
                    
                    <h3 class="h6 fw-bold mb-3">Project Information</h3>
                    <div class="table-responsive">
                        <table class="table">
                            <tr>
                                <th style="width: 150px;">Start Date</th>
                                <td><fmt:formatDate value="${project.startDate}" pattern="MMMM d, yyyy"/></td>
                            </tr>
                            <tr>
                                <th>End Date</th>
                                <td><fmt:formatDate value="${project.endDate}" pattern="MMMM d, yyyy"/></td>
                            </tr>
                            <tr>
                                <th>Created By</th>
                                <td>${project.creator.firstName} ${project.creator.lastName}</td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>

            <div class="card border-0 shadow-sm mb-4">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h2 class="h5 mb-0 text-primary">Project Tasks</h2>
                    <a href="${pageContext.request.contextPath}/task/create?projectId=${project.id}" class="btn btn-primary btn-sm">
                        <i class="bi bi-plus-circle me-1"></i> Add Task
                    </a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Title</th>
                                    <th>Assigned To</th>
                                    <th>Status</th>
                                    <th>Due Date</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="task" items="${projectTasks}">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/task/view/${task.id}" class="text-decoration-none">
                                                ${task.title}
                                            </a>
                                        </td>
                                        <td>${task.assignee.firstName} ${task.assignee.lastName}</td>
                                        <td>
                                            <span class="badge bg-secondary">${task.status}</span>
                                        </td>
                                        <td><fmt:formatDate value="${task.dueDate}" pattern="MMM d, yyyy"/></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/task/edit/${task.id}" 
                                               class="btn btn-sm btn-outline-primary me-1" 
                                               data-bs-toggle="tooltip" 
                                               title="Edit Task">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/task/view/${task.id}" 
                                               class="btn btn-sm btn-outline-info" 
                                               data-bs-toggle="tooltip" 
                                               title="View Details">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty projectTasks}">
                                    <tr>
                                        <td colspan="5" class="text-center py-4">
                                            <div class="text-muted">
                                                <i class="bi bi-inbox fs-4 d-block mb-2"></i>
                                                No tasks found for this project
                                            </div>
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
