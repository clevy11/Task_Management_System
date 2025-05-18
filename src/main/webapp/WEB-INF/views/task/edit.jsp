<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="card-title h4 mb-4">Edit Task</h2>
                    
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-circle me-2"></i>
                            ${errorMessage}
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/task/edit/${task.id}" method="post">
                        <div class="mb-3">
                            <label for="title" class="form-label">Title</label>
                            <input type="text" class="form-control ${not empty titleError ? 'is-invalid' : ''}" 
                                   id="title" name="title" value="${task.title}" required>
                            <c:if test="${not empty titleError}">
                                <div class="invalid-feedback">${titleError}</div>
                            </c:if>
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control ${not empty descriptionError ? 'is-invalid' : ''}" 
                                    id="description" name="description" rows="3">${task.description}</textarea>
                            <c:if test="${not empty descriptionError}">
                                <div class="invalid-feedback">${descriptionError}</div>
                            </c:if>
                        </div>
                        
                        <div class="mb-3">
                            <label for="dueDate" class="form-label">Due Date</label>
                            <input type="date" class="form-control ${not empty dueDateError ? 'is-invalid' : ''}" 
                                   id="dueDate" name="dueDate" 
                                   value="<fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd"/>">
                            <c:if test="${not empty dueDateError}">
                                <div class="invalid-feedback">${dueDateError}</div>
                            </c:if>
                        </div>
                        
                        <div class="mb-3">
                            <label for="status" class="form-label">Status</label>
                            <select class="form-select ${not empty statusError ? 'is-invalid' : ''}" 
                                    id="status" name="status" required>
                                <option value="Pending" ${task.status == 'Pending' ? 'selected' : ''}>Pending</option>
                                <option value="In Progress" ${task.status == 'In Progress' ? 'selected' : ''}>In Progress</option>
                                <option value="Completed" ${task.status == 'Completed' ? 'selected' : ''}>Completed</option>
                            </select>
                            <c:if test="${not empty statusError}">
                                <div class="invalid-feedback">${statusError}</div>
                            </c:if>
                        </div>
                        
                        <div class="mb-3">
                            <label for="assignedTo" class="form-label">Assigned To</label>
                            <select class="form-select ${not empty assignedToError ? 'is-invalid' : ''}" 
                                    id="assignedTo" name="assignedTo" required>
                                <option value="">Select User</option>
                                <c:forEach items="${users}" var="user">
                                    <option value="${user.id}" ${task.assignedTo == user.id ? 'selected' : ''}>
                                        ${user.fullName}
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty assignedToError}">
                                <div class="invalid-feedback">${assignedToError}</div>
                            </c:if>
                        </div>
                        
                        <div class="mb-4">
                            <label for="projectId" class="form-label">Project</label>
                            <select class="form-select ${not empty projectIdError ? 'is-invalid' : ''}" 
                                    id="projectId" name="projectId">
                                <option value="">No Project</option>
                                <c:forEach items="${projects}" var="project">
                                    <option value="${project.id}" ${task.projectId == project.id ? 'selected' : ''}>
                                        ${project.name}
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty projectIdError}">
                                <div class="invalid-feedback">${projectIdError}</div>
                            </c:if>
                        </div>
                        
                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/task/view/${task.id}" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-1"></i> Back
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check2 me-1"></i> Update Task
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
