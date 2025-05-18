<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container py-4">
    <div class="row">
        <div class="col-md-8">
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2 class="card-title h4 mb-0">${task.title}</h2>
                        <div class="task-actions">
                            <c:if test="${sessionScope.user.admin || sessionScope.user.id == task.assignedTo || sessionScope.user.id == task.createdBy}">
                                <a href="${pageContext.request.contextPath}/task/edit/${task.id}" class="btn btn-outline-primary btn-sm">
                                    <i class="bi bi-pencil me-1"></i> Edit
                                </a>
                                <a href="${pageContext.request.contextPath}/task/status/${task.id}" class="btn btn-outline-secondary btn-sm">
                                    <i class="bi bi-arrow-repeat me-1"></i> Update Status
                                </a>
                            </c:if>
                            <c:if test="${sessionScope.user.admin || sessionScope.user.id == task.createdBy}">
                                <button type="button" class="btn btn-outline-danger btn-sm" data-bs-toggle="modal" data-bs-target="#deleteTaskModal">
                                    <i class="bi bi-trash me-1"></i> Delete
                                </button>
                            </c:if>
                        </div>
                    </div>

                    <div class="task-details">
                        <div class="mb-3">
                            <h5 class="h6">Description</h5>
                            <p class="mb-0">${task.description}</p>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <h5 class="h6">Status</h5>
                                <span class="badge bg-${task.status == 'DONE' ? 'success' : task.status == 'IN_PROGRESS' ? 'primary' : task.status == 'REVIEW' ? 'warning' : 'secondary'}">
                                    ${task.status}
                                </span>
                            </div>
                            <div class="col-md-6">
                                <h5 class="h6">Due Date</h5>
                                <p class="mb-0">
                                    <fmt:formatDate value="${task.dueDate}" pattern="MMMM d, yyyy" />
                                </p>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <h5 class="h6">Assigned To</h5>
                                <p class="mb-0">${task.assignee.fullName}</p>
                            </div>
                            <div class="col-md-6">
                                <h5 class="h6">Created By</h5>
                                <p class="mb-0">${task.creator.fullName}</p>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <h5 class="h6">Project</h5>
                                <p class="mb-0">
                                    <c:if test="${not empty task.project}">
                                        <a href="${pageContext.request.contextPath}/project/view/${task.project.id}">
                                            ${task.project.name}
                                        </a>
                                    </c:if>
                                    <c:if test="${empty task.project}">
                                        <span class="text-muted">No Project</span>
                                    </c:if>
                                </p>
                            </div>
                            <div class="col-md-6">
                                <h5 class="h6">Created At</h5>
                                <p class="mb-0">
                                    <fmt:formatDate value="${task.createdAt}" pattern="MMM d, yyyy HH:mm" />
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Task Activity Log -->
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <h3 class="h5 mb-4">Activity Log</h3>
                    
                    <div class="timeline">
                        <c:forEach items="${logs}" var="log">
                            <div class="timeline-item mb-3">
                                <div class="d-flex">
                                    <div class="timeline-marker me-3">
                                        <i class="bi bi-circle-fill"></i>
                                    </div>
                                    <div class="timeline-content">
                                        <p class="mb-0">
                                            Status changed from 
                                            <span class="badge bg-secondary">${empty log.oldStatus ? 'New' : log.oldStatus}</span> 
                                            to 
                                            <span class="badge bg-primary">${log.newStatus}</span>
                                        </p>
                                        <small class="text-muted">
                                            <fmt:formatDate value="${log.changedAt}" pattern="MMM d, yyyy HH:mm" />
                                            by ${log.changer.firstName} ${log.changer.lastName}
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                        
                        <c:if test="${empty logs}">
                            <p class="text-muted mb-0">No activity recorded yet.</p>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <!-- Task Timeline -->
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <h3 class="h5 mb-4">Timeline</h3>
                    <div class="task-timeline">
                        <div class="timeline-item mb-3">
                            <div class="d-flex">
                                <div class="timeline-marker me-3">
                                    <i class="bi bi-check-circle-fill text-success"></i>
                                </div>
                                <div class="timeline-content">
                                    <p class="mb-0">Task Created</p>
                                    <small class="text-muted">
                                        <fmt:formatDate value="${task.createdAt}" pattern="MMM d, yyyy HH:mm" />
                                    </small>
                                </div>
                            </div>
                        </div>
                        
                        <c:if test="${not empty task.updatedAt}">
                            <div class="timeline-item">
                                <div class="d-flex">
                                    <div class="timeline-marker me-3">
                                        <i class="bi bi-pencil-fill text-primary"></i>
                                    </div>
                                    <div class="timeline-content">
                                        <p class="mb-0">Last Updated</p>
                                        <small class="text-muted">
                                            <fmt:formatDate value="${task.updatedAt}" pattern="MMM d, yyyy HH:mm" />
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Delete Task Modal -->
<div class="modal fade" id="deleteTaskModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Delete Task</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete this task? This action cannot be undone.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <a href="${pageContext.request.contextPath}/task/delete/${task.id}" class="btn btn-danger">Delete</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
