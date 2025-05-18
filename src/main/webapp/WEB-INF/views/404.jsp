<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="common/header.jsp" />

<div class="mt-5 text-center">
    <div class="card" style="max-width: 600px; margin: 0 auto;">
        <div class="card-body">
            <h1 class="mb-4">404 - Page Not Found</h1>
            <p class="mb-4">The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.</p>
            
            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Go to Dashboard</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp" />
