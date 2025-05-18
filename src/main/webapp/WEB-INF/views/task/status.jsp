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
                    
                    <c:if test="${not empty errors}">
                        <div class="alert alert-danger">
                            <ul class="mb-0">
                                <c:forEach items="${errors}" var="error">
                                    <li>${error.value}</li>
                                </c:forEach>
                            </ul>
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
                            <label for="status" class="form-label">New Status</label>
                            <select class="form-select" id="status" name="status" required>
                                <option value="">Select Status</option>
                                <option value="Pending" ${task.status == 'Pending' ? 'selected' : ''}>Pending</option>
                                <option value="In Progress" ${task.status == 'In Progress' ? 'selected' : ''}>In Progress</option>
                                <option value="Completed" ${task.status == 'Completed' ? 'selected' : ''}>Completed</option>
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
