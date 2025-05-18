<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>${project.id != null ? 'Edit Project' : 'Add New Project'}</h1>
        <a href="${pageContext.request.contextPath}/project" class="btn btn-secondary">Back to Projects</a>
    </div>
    
    <div class="card">
        <div class="card-body">
            <c:if test="${!sessionScope.user.isAdmin()}">
                <div class="alert alert-warning">
                    Only administrators can create or edit projects.
                </div>
            </c:if>
            
            <c:if test="${sessionScope.user.isAdmin()}">
                <form action="${pageContext.request.contextPath}/project" method="post">
                    <input type="hidden" name="action" value="${project.id != null ? 'update' : 'add'}">
                    <c:if test="${project.id != null}">
                        <input type="hidden" name="id" value="${project.id}">
                    </c:if>
                    
                    <div class="form-group">
                        <label for="name" class="form-label">Project Name</label>
                        <input type="text" id="name" name="name" class="form-control" value="${project.name}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="description" class="form-label">Description</label>
                        <textarea id="description" name="description" class="form-control" rows="4" required>${project.description}</textarea>
                    </div>
                    
                    <div class="d-flex justify-content-end mt-4">
                        <button type="submit" class="btn btn-primary">${project.id != null ? 'Update Project' : 'Create Project'}</button>
                    </div>
                </form>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
