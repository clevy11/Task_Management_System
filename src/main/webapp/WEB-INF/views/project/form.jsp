<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>${empty project.id ? 'Add New Project' : 'Edit Project'}</h1>
        <a href="${pageContext.request.contextPath}/project" class="btn btn-secondary">
            <i class="bi bi-arrow-left me-1"></i>Back to Projects
        </a>
    </div>
    
    <div class="card">
        <div class="card-body">
            <c:if test="${sessionScope.user.role ne 'admin'}">
                <div class="alert alert-warning">
                    <i class="bi bi-exclamation-triangle me-2"></i>Only administrators can create or edit projects.
                </div>
            </c:if>
            
            <c:if test="${sessionScope.user.role eq 'admin'}">
                <c:choose>
                    <c:when test="${empty project.id}">
                        <form action="${pageContext.request.contextPath}/project/create" method="POST" class="needs-validation" novalidate>
                    </c:when>
                    <c:otherwise>
                        <form action="${pageContext.request.contextPath}/project/edit/${project.id}" method="POST" class="needs-validation" novalidate>
                    </c:otherwise>
                </c:choose>
                    <div class="mb-3">
                        <label for="name" class="form-label">Project Name <span class="text-danger">*</span></label>
                        <input type="text" 
                               class="form-control ${not empty errors.name ? 'is-invalid' : ''}" 
                               id="name" 
                               name="name" 
                               value="${project.name}"
                               required>
                        <c:if test="${not empty errors.name}">
                            <div class="invalid-feedback">${errors.name}</div>
                        </c:if>
                    </div>
                    
                    <div class="mb-3">
                        <label for="description" class="form-label">Description <span class="text-danger">*</span></label>
                        <textarea class="form-control ${not empty errors.description ? 'is-invalid' : ''}" 
                                  id="description" 
                                  name="description" 
                                  rows="4" 
                                  required>${project.description}</textarea>
                        <c:if test="${not empty errors.description}">
                            <div class="invalid-feedback">${errors.description}</div>
                        </c:if>
                    </div>
                    
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="startDate" class="form-label">Start Date <span class="text-danger">*</span></label>
                            <input type="date" 
                                   class="form-control ${not empty errors.startDate ? 'is-invalid' : ''}" 
                                   id="startDate" 
                                   name="startDate" 
                                   value="<fmt:formatDate value='${project.startDate}' pattern='yyyy-MM-dd'/>"
                                   required>
                            <c:if test="${not empty errors.startDate}">
                                <div class="invalid-feedback">${errors.startDate}</div>
                            </c:if>
                        </div>
                        
                        <div class="col-md-6 mb-3">
                            <label for="endDate" class="form-label">End Date <span class="text-danger">*</span></label>
                            <input type="date" 
                                   class="form-control ${not empty errors.endDate ? 'is-invalid' : ''}" 
                                   id="endDate" 
                                   name="endDate" 
                                   value="<fmt:formatDate value='${project.endDate}' pattern='yyyy-MM-dd'/>"
                                   required>
                            <c:if test="${not empty errors.endDate}">
                                <div class="invalid-feedback">${errors.endDate}</div>
                            </c:if>
                        </div>
                    </div>
                    
                    <div class="d-flex justify-content-end gap-2 mt-4">
                        <a href="${pageContext.request.contextPath}/project" class="btn btn-light">Cancel</a>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save me-1"></i>${empty project.id ? 'Create Project' : 'Update Project'}
                        </button>
                    </div>
                </form>
            </c:if>
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
})()

// Date validation
document.getElementById('endDate').addEventListener('change', function() {
    var startDate = document.getElementById('startDate').value;
    var endDate = this.value;
    
    if (startDate && endDate && startDate > endDate) {
        this.setCustomValidity('End date must be after start date');
    } else {
        this.setCustomValidity('');
    }
});

document.getElementById('startDate').addEventListener('change', function() {
    var endDate = document.getElementById('endDate');
    if (endDate.value) {
        if (this.value > endDate.value) {
            endDate.setCustomValidity('End date must be after start date');
        } else {
            endDate.setCustomValidity('');
        }
    }
});
</script>

<jsp:include page="../common/footer.jsp" />
