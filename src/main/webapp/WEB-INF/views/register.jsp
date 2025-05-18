<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="common/header.jsp" />

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card auth-card">
                <div class="card-body">
                    <div class="text-center mb-4">
                        <h2>Register</h2>
                        <p class="text-muted">Create a new account</p>
                    </div>
                    
                    <!-- Error Message Display -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            ${errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/auth" method="post" id="registerForm" novalidate>
                        <input type="hidden" name="action" value="register">
                        
                        <div class="mb-3">
                            <label for="firstName" class="form-label">First Name</label>
                            <input type="text" id="firstName" name="firstName" class="form-control ${not empty errors.firstName ? 'is-invalid' : ''}" 
                                   value="${firstName}" required>
                            <c:if test="${not empty errors.firstName}">
                                <div class="invalid-feedback">${errors.firstName}</div>
                            </c:if>
                        </div>
                        
                        <div class="mb-3">
                            <label for="lastName" class="form-label">Last Name</label>
                            <input type="text" id="lastName" name="lastName" class="form-control ${not empty errors.lastName ? 'is-invalid' : ''}" 
                                   value="${lastName}" required>
                            <c:if test="${not empty errors.lastName}">
                                <div class="invalid-feedback">${errors.lastName}</div>
                            </c:if>
                        </div>
                        
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
                            <small class="form-text text-muted">Password must be at least 6 characters long</small>
                        </div>
                        
                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label">Confirm Password</label>
                            <div class="input-group">
                                <input type="password" id="confirmPassword" name="confirmPassword" 
                                       class="form-control ${not empty errors.confirmPassword ? 'is-invalid' : ''}" required>
                                <button class="btn btn-outline-secondary" type="button" onclick="togglePasswordVisibility('confirmPassword')">
                                    <i class="bi bi-eye" id="confirmPassword-toggle-icon"></i>
                                </button>
                                <c:if test="${not empty errors.confirmPassword}">
                                    <div class="invalid-feedback">${errors.confirmPassword}</div>
                                </c:if>
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Register</button>
                        </div>
                    </form>
                    
                    <div class="text-center mt-3">
                        <p>Already have an account? <a href="${pageContext.request.contextPath}/auth?action=showLogin">Login</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Client-side validation script -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.getElementById('registerForm');
        
        form.addEventListener('submit', function(event) {
            let isValid = true;
            
            // First Name validation
            const firstName = document.getElementById('firstName');
            if (firstName.value.trim() === '') {
                setInvalid(firstName, 'First name is required');
                isValid = false;
            } else if (firstName.value.length < 2 || firstName.value.length > 50) {
                setInvalid(firstName, 'First name must be between 2 and 50 characters');
                isValid = false;
            } else {
                setValid(firstName);
            }
            
            // Last Name validation
            const lastName = document.getElementById('lastName');
            if (lastName.value.trim() === '') {
                setInvalid(lastName, 'Last name is required');
                isValid = false;
            } else if (lastName.value.length < 2 || lastName.value.length > 50) {
                setInvalid(lastName, 'Last name must be between 2 and 50 characters');
                isValid = false;
            } else {
                setValid(lastName);
            }
            
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
            } else if (password.value.length < 6) {
                setInvalid(password, 'Password must be at least 6 characters long');
                isValid = false;
            } else {
                setValid(password);
            }
            
            // Confirm Password validation
            const confirmPassword = document.getElementById('confirmPassword');
            if (confirmPassword.value.trim() === '') {
                setInvalid(confirmPassword, 'Please confirm your password');
                isValid = false;
            } else if (confirmPassword.value !== password.value) {
                setInvalid(confirmPassword, 'Passwords do not match');
                isValid = false;
            } else {
                setValid(confirmPassword);
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
