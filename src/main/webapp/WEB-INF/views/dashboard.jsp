<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="common/header.jsp" />

<!-- Modern Dashboard with Bootstrap 5 styling -->
<div class="container py-4">
    <div class="row mb-4">
        <div class="col-12">
            <div class="bg-primary text-white p-4 rounded shadow">
                <h1 class="display-5 fw-bold">Dashboard</h1>
                <p class="lead">Welcome back, ${sessionScope.user.firstName}!</p>
            </div>
        </div>
    </div>
    
    <!-- Statistics Cards -->
    <div class="row mb-4">
        <div class="col-md-3 mb-3">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body text-center">
                    <div class="display-4 text-primary mb-2">${taskStats.totalTasks}</div>
                    <h5 class="card-title text-muted">Total Tasks</h5>
                    <p class="card-text small text-muted">All assigned tasks</p>
                </div>
            </div>
        </div>
        
        <div class="col-md-3 mb-3">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body text-center">
                    <div class="display-4 text-warning mb-2">${taskStats.pendingTasks}</div>
                    <h5 class="card-title text-muted">Pending Tasks</h5>
                    <p class="card-text small text-muted">Tasks waiting to be started</p>
                </div>
            </div>
        </div>
        
        <div class="col-md-3 mb-3">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body text-center">
                    <div class="display-4 text-info mb-2">${taskStats.inProgressTasks}</div>
                    <h5 class="card-title text-muted">In Progress</h5>
                    <p class="card-text small text-muted">Tasks currently in progress</p>
                </div>
            </div>
        </div>
        
        <div class="col-md-3 mb-3">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body text-center">
                    <div class="display-4 text-success mb-2">${taskStats.completedTasks}</div>
                    <h5 class="card-title text-muted">Completed</h5>
                    <p class="card-text small text-muted">Successfully completed tasks</p>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Recent Tasks -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
            <h2 class="h5 mb-0 text-primary">Recent Tasks</h2>
            <a href="${pageContext.request.contextPath}/task/create" class="btn btn-primary btn-sm">
                <i class="bi bi-plus-circle me-1"></i> Add New Task
            </a>
        </div>
        <div class="card-body">
            <c:if test="${empty recentTasks}">
                <div class="text-center py-5">
                    <i class="bi bi-clipboard-x display-4 text-muted mb-3"></i>
                    <p class="text-muted">No tasks found. Start by creating a new task.</p>
                </div>
            </c:if>
            
            <c:if test="${not empty recentTasks}">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>Title</th>
                                <th>Project</th>
                                <th>Status</th>
                                <th>Due Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="task" items="${recentTasks}">
                                <tr>
                                    <td class="align-middle fw-medium">${task.title}</td>
                                    <td class="align-middle">${task.project.name}</td>
                                    <td class="align-middle">
                                        <c:choose>
                                            <c:when test="${task.status == 'PENDING'}">
                                                <span class="badge bg-warning text-dark">Pending</span>
                                            </c:when>
                                            <c:when test="${task.status == 'IN_PROGRESS'}">
                                                <span class="badge bg-info">In Progress</span>
                                            </c:when>
                                            <c:when test="${task.status == 'COMPLETED'}">
                                                <span class="badge bg-success">Completed</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${task.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="align-middle">
                                        <fmt:formatDate value="${task.dueDate}" pattern="MMM dd, yyyy" />
                                    </td>
                                    <td class="align-middle">
                                        <div class="btn-group btn-group-sm" role="group">
                                            <a href="${pageContext.request.contextPath}/task/view/${task.id}" class="btn btn-outline-primary">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/task/edit/${task.id}" class="btn btn-outline-secondary">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
        <div class="card-footer bg-white border-top-0 text-end">
            <a href="${pageContext.request.contextPath}/tasks" class="btn btn-outline-primary btn-sm">
                View All Tasks
            </a>
        </div>
    </div>
    
    <c:if test="${sessionScope.user.isAdmin()}">
        <div class="card border-0 shadow-sm">
            <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
                <h2 class="h5 mb-0 text-primary">Projects</h2>
                <a href="${pageContext.request.contextPath}/project/create" class="btn btn-primary btn-sm">
                    <i class="bi bi-plus-circle me-1"></i> Add New Project
                </a>
            </div>
            <div class="card-body">
                <c:if test="${empty projects}">
                    <div class="text-center py-5">
                        <i class="bi bi-folder-x display-4 text-muted mb-3"></i>
                        <p class="text-muted">No projects found. Start by creating a new project.</p>
                    </div>
                </c:if>
                
                <c:if test="${not empty projects}">
                    <div class="row">
                        <c:forEach var="project" items="${projects}">
                            <div class="col-md-4 mb-4">
                                <div class="card h-100 border-0 shadow-sm">
                                    <div class="card-body">
                                        <h5 class="card-title">${project.name}</h5>
                                        <p class="card-text text-muted small mb-3">${project.description}</p>
                                        <div class="d-flex justify-content-between align-items-center">
                                            <small class="text-muted">
                                                <fmt:formatDate value="${project.startDate}" pattern="MMM dd, yyyy" />
                                            </small>
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/project/view/${project.id}" class="btn btn-outline-primary">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/project/edit/${project.id}" class="btn btn-outline-secondary">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
            <div class="card-footer bg-white border-top-0 text-end">
                <a href="${pageContext.request.contextPath}/projects" class="btn btn-outline-primary btn-sm">
                    View All Projects
                </a>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="common/footer.jsp" />
