<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Projects</h1>
        <c:if test="${sessionScope.user.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/project/create" class="btn btn-primary">
                <i class="bi bi-plus-circle me-1"></i>Add New Project
            </a>
        </c:if>
    </div>
    
    <div class="card">
        <div class="card-body">
            <c:if test="${empty projects}">
                <div class="text-center py-5">
                    <i class="bi bi-folder-x display-4 text-muted mb-3"></i>
                    <p class="text-muted">No projects found. 
                    <c:if test="${sessionScope.user.role == 'ADMIN'}">
                        Start by creating a new project.
                    </c:if>
                    </p>
                </div>
            </c:if>
            
            <c:if test="${not empty projects}">
                <div class="row">
                    <c:forEach var="project" items="${projects}">
                        <div class="col-md-4 mb-4">
                            <div class="card h-100 border-0 shadow-sm">
                                <div class="card-body">
                                    <h3 class="h5 mb-3">${project.name}</h3>
                                    <p class="text-muted mb-3">${project.description}</p>
                                    <div class="d-flex justify-content-between text-muted small mb-3">
                                        <span><i class="bi bi-list-task me-1"></i>${project.taskCount} Tasks</span>
                                        <span><i class="bi bi-calendar me-1"></i>
                                            <fmt:formatDate value="${project.startDate}" pattern="MMM dd, yyyy" /> - 
                                            <fmt:formatDate value="${project.endDate}" pattern="MMM dd, yyyy" />
                                        </span>
                                    </div>
                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/project/view/${project.id}" 
                                           class="btn btn-outline-primary btn-sm flex-grow-1">
                                            <i class="bi bi-eye me-1"></i>View
                                        </a>
                                        <c:if test="${sessionScope.user.role == 'ADMIN'}">
                                            <a href="${pageContext.request.contextPath}/project/edit/${project.id}" 
                                               class="btn btn-outline-secondary btn-sm">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <button onclick="confirmDelete('${pageContext.request.contextPath}/project/delete/${project.id}')" 
                                                    class="btn btn-outline-danger btn-sm">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script>
function confirmDelete(url) {
    if (confirm('Are you sure you want to delete this project? All associated tasks will also be deleted.')) {
        window.location.href = url;
    }
}
</script>

<jsp:include page="../common/footer.jsp" />
