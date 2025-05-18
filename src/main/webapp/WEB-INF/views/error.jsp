<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="common/header.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="text-center mb-4">
                <i class="bi bi-exclamation-triangle text-warning" style="font-size: 4rem;"></i>
                <h1 class="h3 mt-3">Oops! Something went wrong</h1>
                <p class="text-muted">We encountered an unexpected error while processing your request.</p>
            </div>

            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">
                            <i class="bi bi-info-circle me-2"></i>
                            ${errorMessage}
                        </div>
                    </c:if>

                    <div class="text-center mt-4">
                        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary me-2">
                            <i class="bi bi-house me-1"></i> Go to Dashboard
                        </a>
                        <button onclick="history.back()" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i> Go Back
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp" />
