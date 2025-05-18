<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="card-title h4 mb-4">Update Task Status</h2>
                    
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-circle me-2"></i>
                            ${errorMessage}
                        </div>
                    </c:if>
                    
                    <div class="mb-4">
                        <h5>Task Details</h5>
                        <p class="mb-1"><strong>Title:</strong> ${task.title}</p>
                        <p class="mb-1"><strong>Current Status:</strong> ${task.status}</p>
                        <p class="mb-0"><strong>Due Date:</strong> 
                            <fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd" />
                        </p>
                    </div>
                    
                    <form action="${pageContext.request.contextPath}/task/status/${task.id}" method="post">
                        <div class="mb-3">
                            <label for="newStatus" class="form-label">New Status</label>
                            <select class="form-select" id="newStatus" name="newStatus" required>
                                <option value="">Select Status</option>
                                <option value="TODO" ${task.status == 'TODO' ? 'selected' : ''}>To Do</option>
                                <option value="IN_PROGRESS" ${task.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                                <option value="REVIEW" ${task.status == 'REVIEW' ? 'selected' : ''}>Review</option>
                                <option value="DONE" ${task.status == 'DONE' ? 'selected' : ''}>Done</option>
                            </select>
                        </div>
                        
                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/task/view/${task.id}" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-1"></i> Back
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check2 me-1"></i> Update Status
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
