<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container py-4">
    <div class="row">
        <div class="col-md-8 mx-auto">
            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h1 class="h4 mb-0 text-primary">Edit Project</h1>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/project/edit/${project.id}" method="POST" class="needs-validation" novalidate>
                        <div class="mb-3">
                            <label for="name" class="form-label">Project Name</label>
                            <input type="text" class="form-control" id="name" name="name" value="${project.name}" required>
                            <div class="invalid-feedback">
                                Please provide a project name.
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="4" required>${project.description}</textarea>
                            <div class="invalid-feedback">
                                Please provide a project description.
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="startDate" class="form-label">Start Date</label>
                                <input type="date" class="form-control" id="startDate" name="startDate" 
                                       value="<fmt:formatDate value='${project.startDate}' pattern='yyyy-MM-dd'/>" required>
                                <div class="invalid-feedback">
                                    Please select a start date.
                                </div>
                            </div>

                            <div class="col-md-6">
                                <label for="endDate" class="form-label">End Date</label>
                                <input type="date" class="form-control" id="endDate" name="endDate" 
                                       value="<fmt:formatDate value='${project.endDate}' pattern='yyyy-MM-dd'/>" required>
                                <div class="invalid-feedback">
                                    Please select an end date.
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="status" class="form-label">Status</label>
                            <select class="form-select" id="status" name="status" required>
                                <option value="PLANNING" ${project.status == 'PLANNING' ? 'selected' : ''}>Planning</option>
                                <option value="IN_PROGRESS" ${project.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                                <option value="COMPLETED" ${project.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                                <option value="ON_HOLD" ${project.status == 'ON_HOLD' ? 'selected' : ''}>On Hold</option>
                            </select>
                            <div class="invalid-feedback">
                                Please select a status.
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="priority" class="form-label">Priority</label>
                            <select class="form-select" id="priority" name="priority" required>
                                <option value="LOW" ${project.priority == 'LOW' ? 'selected' : ''}>Low</option>
                                <option value="MEDIUM" ${project.priority == 'MEDIUM' ? 'selected' : ''}>Medium</option>
                                <option value="HIGH" ${project.priority == 'HIGH' ? 'selected' : ''}>High</option>
                            </select>
                            <div class="invalid-feedback">
                                Please select a priority level.
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="teamMembers" class="form-label">Team Members</label>
                            <select class="form-select" id="teamMembers" name="teamMembers" multiple required>
                                <c:forEach var="user" items="${users}">
                                    <option value="${user.id}" 
                                            <c:if test="${project.teamMembers.contains(user)}">selected</c:if>>
                                        ${user.firstName} ${user.lastName}
                                    </option>
                                </c:forEach>
                            </select>
                            <div class="form-text">Hold Ctrl (Windows) or Command (Mac) to select multiple team members.</div>
                            <div class="invalid-feedback">
                                Please select at least one team member.
                            </div>
                        </div>

                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/projects" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-1"></i> Back to Projects
                            </a>
                            <div>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-save me-1"></i> Save Changes
                                </button>
                            </div>
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

    // Date validation
    var startDate = document.getElementById('startDate')
    var endDate = document.getElementById('endDate')

    startDate.addEventListener('change', function() {
        endDate.min = startDate.value
    })

    endDate.addEventListener('change', function() {
        startDate.max = endDate.value
    })
})()
</script>

<jsp:include page="../common/footer.jsp" />
