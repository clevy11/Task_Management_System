<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp" />

<div class="mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Projects</h1>
        <c:if test="${sessionScope.user.isAdmin()}">
            <a href="${pageContext.request.contextPath}/project?action=showAddForm" class="btn btn-primary">Add New Project</a>
        </c:if>
    </div>
    
    <div class="card">
        <div class="card-body">
            <c:if test="${empty projects}">
                <p class="text-center">No projects found. 
                <c:if test="${sessionScope.user.isAdmin()}">
                    Start by creating a new project.
                </c:if>
                </p>
            </c:if>
            
            <c:if test="${not empty projects}">
                <div class="project-grid">
                    <c:forEach var="project" items="${projects}">
                        <div class="card project-card">
                            <div class="card-body">
                                <h3 class="project-title">${project.name}</h3>
                                <p class="project-description">${project.description}</p>
                                <div class="project-meta">
                                    <span>Tasks: ${project.taskCount}</span>
                                    <span>Created: <fmt:formatDate value="${project.createdAt}" pattern="MMM dd, yyyy" /></span>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="project-actions">
                                    <a href="${pageContext.request.contextPath}/project?action=view&id=${project.id}" class="btn btn-info btn-sm">View</a>
                                    <c:if test="${sessionScope.user.isAdmin()}">
                                        <a href="${pageContext.request.contextPath}/project?action=showEditForm&id=${project.id}" class="btn btn-secondary btn-sm">Edit</a>
                                        <a href="${pageContext.request.contextPath}/project?action=delete&id=${project.id}" 
                                           class="btn btn-danger btn-sm"
                                           onclick="confirmDelete(event, 'project')">Delete</a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
