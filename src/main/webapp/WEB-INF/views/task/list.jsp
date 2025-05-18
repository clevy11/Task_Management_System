<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Tasks</h1>
        <a href="${pageContext.request.contextPath}/task/create" class="btn btn-primary">Add New Task</a>
    </div>

    <!-- Filters -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/tasks" method="get" class="row g-3">
                <div class="col-md-4">
                    <label for="status" class="form-label">Status</label>
                    <select name="status" id="status" class="form-select">
                        <option value="">All Statuses</option>
                        <option value="TODO" ${param.status == 'TODO' ? 'selected' : ''}>To Do</option>
                        <option value="IN_PROGRESS" ${param.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                        <option value="DONE" ${param.status == 'DONE' ? 'selected' : ''}>Done</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="project" class="form-label">Project</label>
                    <select name="project" id="project" class="form-select">
                        <option value="">All Projects</option>
                        <c:forEach items="${projects}" var="project">
                            <option value="${project.id}" ${param.project == project.id ? 'selected' : ''}>${project.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary">Apply Filters</button>
                    <a href="${pageContext.request.contextPath}/tasks" class="btn btn-secondary ms-2">Clear</a>
                </div>
            </form>
        </div>
    </div>

    <!-- Tasks List -->
    <div class="card">
        <div class="card-body">
            <c:if test="${empty tasks}">
                <p class="text-center">No tasks found matching your criteria.</p>
            </c:if>
            <c:if test="${not empty tasks}">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Project</th>
                                <th>Assignee</th>
                                <th>Due Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${tasks}" var="task">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/task/view/${task.id}">${task.title}</a>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${task.projectId > 0 && task.project != null && task.project.name != null}">
                                                ${task.project.name}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">No Project</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${task.assignedTo > 0 && task.assignee != null && task.assignee.firstName != null}">
                                                ${task.assignee.firstName} ${task.assignee.lastName}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Unassigned</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${task.dueDate != null}">
                                            <fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" />
                                        </c:if>
                                        <c:if test="${task.dueDate == null}">
                                            <span class="text-muted">No due date</span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${task.status == 'TODO'}">
                                                <span class="badge bg-secondary">To Do</span>
                                            </c:when>
                                            <c:when test="${task.status == 'IN_PROGRESS'}">
                                                <span class="badge bg-primary">In Progress</span>
                                            </c:when>
                                            <c:when test="${task.status == 'DONE'}">
                                                <span class="badge bg-success">Done</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${task.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="btn-group">
                                            <a href="${pageContext.request.contextPath}/task/edit/${task.id}" class="btn btn-sm btn-outline-primary">Edit</a>
                                            <button type="button" class="btn btn-sm btn-outline-danger" onclick="confirmDelete(${task.id})">Delete</button>
                                        </div>
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

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirm Delete</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete this task?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <a href="#" id="confirmDeleteButton" class="btn btn-danger">Delete</a>
            </div>
        </div>
    </div>
</div>

<script>
function confirmDelete(taskId) {
    document.getElementById('confirmDeleteButton').href = '${pageContext.request.contextPath}/task/delete/' + taskId;
    new bootstrap.Modal(document.getElementById('deleteModal')).show();
}
</script>

<jsp:include page="../common/footer.jsp" />
