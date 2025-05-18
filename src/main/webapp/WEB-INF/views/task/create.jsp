<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container py-4">
    <div class="row">
        <div class="col-md-8 mx-auto">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h1 class="h4 mb-0 text-primary">Create New Task</h1>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/task/create" method="POST" class="needs-validation" novalidate>
                        <input type="hidden" name="projectId" value="${param.projectId}">
                        
                        <div class="mb-3">
                            <label for="title" class="form-label">Task Title</label>
                            <input type="text" class="form-control" id="title" name="title" required>
                            <div class="invalid-feedback">
                                Please provide a task title.
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="4" required></textarea>
                            <div class="invalid-feedback">
                                Please provide a task description.
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="dueDate" class="form-label">Due Date</label>
                                <input type="date" class="form-control" id="dueDate" name="dueDate" required>
                                <div class="invalid-feedback">
                                    Please select a due date.
                                </div>
                            </div>

                            <div class="col-md-6">
                                <label for="status" class="form-label">Status</label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="PENDING">Pending</option>
                                    <option value="IN_PROGRESS">In Progress</option>
                                    <option value="COMPLETED">Completed</option>
                                </select>
                                <div class="invalid-feedback">
                                    Please select a status.
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="assignedTo" class="form-label">Assign To</label>
                            <select class="form-select" id="assignedTo" name="assignedTo" required>
                                <option value="">Select a team member</option>
                                <c:forEach var="user" items="${users}">
                                    <option value="${user.id}">${user.firstName} ${user.lastName}</option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback">
                                Please select a team member.
                            </div>
                        </div>

                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/projects" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-1"></i> Back to Projects
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-plus-circle me-1"></i> Create Task
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
// Form validation
(function () {
    'use strict'
    var forms = document.querySelectorAll('.needs-validation')
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }
            form.classList.add('was-validated')
        }, false)
    })

    // Set minimum date to today
    var today = new Date().toISOString().split('T')[0]
    document.getElementById('dueDate').min = today
})()
</script>

<jsp:include page="../common/footer.jsp" />
