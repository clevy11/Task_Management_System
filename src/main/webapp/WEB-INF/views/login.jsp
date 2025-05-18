<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="common/header.jsp" />

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card auth-card">
                <div class="card-body">
                    <div class="text-center mb-4">
                        <h2>Login</h2>
                        <p class="text-muted">Sign in to your account</p>
                    </div>
                    
                    <!-- Error Message Display -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            ${errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <!-- Success Message Display -->
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            ${successMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/auth" method="post" id="loginForm" novalidate>
                        <input type="hidden" name="action" value="login">
                        
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" id="email" name="email" class="form-control ${not empty errors.email ? 'is-invalid' : ''}" 
                                   value="${email}" required>
                            <c:if test="${not empty errors.email}">
                                <div class="invalid-feedback">${errors.email}</div>
                            </c:if>
                        </div>
                        
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <div class="input-group">
                                <input type="password" id="password" name="password" 
                                       class="form-control ${not empty errors.password ? 'is-invalid' : ''}" required>
                                <button class="btn btn-outline-secondary" type="button" onclick="togglePasswordVisibility('password')">
                                    <i class="bi bi-eye" id="password-toggle-icon"></i>
                                </button>
                                <c:if test="${not empty errors.password}">
                                    <div class="invalid-feedback">${errors.password}</div>
                                </c:if>
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Login</button>
                        </div>
                    </form>
                    
                    <div class="text-center mt-3">
                        <p>Don't have an account? <a href="${pageContext.request.contextPath}/auth?action=showRegister">Register</a></p>
                    </div>
                </div>
            </div>
            
            <!-- Admin Login Info Card -->
            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title">Admin Access</h5>
                    <p class="card-text">For admin functionality, use:</p>
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item">Email: admin@novatech.com</li>
                        <li class="list-group-item">Password: admin123</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Client-side validation script -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.getElementById('loginForm');
        
        form.addEventListener('submit', function(event) {
            let isValid = true;
            
            // Email validation
            const email = document.getElementById('email');
            const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
            if (email.value.trim() === '') {
                setInvalid(email, 'Email is required');
                isValid = false;
            } else if (!emailRegex.test(email.value)) {
                setInvalid(email, 'Please enter a valid email address');
                isValid = false;
            } else {
                setValid(email);
            }
            
            // Password validation
            const password = document.getElementById('password');
            if (password.value.trim() === '') {
                setInvalid(password, 'Password is required');
                isValid = false;
            } else {
                setValid(password);
            }
            
            if (!isValid) {
                event.preventDefault();
            }
        });
        
        function setInvalid(element, message) {
            element.classList.add('is-invalid');
            element.classList.remove('is-valid');
            
            // Create or update feedback div
            let feedback = element.parentNode.querySelector('.invalid-feedback');
            if (!feedback) {
                feedback = document.createElement('div');
                feedback.className = 'invalid-feedback';
                element.parentNode.appendChild(feedback);
            }
            feedback.textContent = message;
        }
        
        function setValid(element) {
            element.classList.remove('is-invalid');
            element.classList.add('is-valid');
        }
    });
    
    function togglePasswordVisibility(inputId) {
        const passwordInput = document.getElementById(inputId);
        const toggleIcon = document.getElementById(inputId + '-toggle-icon');
        
        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            toggleIcon.classList.remove('bi-eye');
            toggleIcon.classList.add('bi-eye-slash');
        } else {
            passwordInput.type = 'password';
            toggleIcon.classList.remove('bi-eye-slash');
            toggleIcon.classList.add('bi-eye');
        }
    }
</script>

<jsp:include page="common/footer.jsp" />
