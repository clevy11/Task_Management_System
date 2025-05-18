// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize date pickers for all date input fields
    initializeDatePickers();
    
    // Initialize alert dismissal
    initializeAlertDismissal();
    
    // Initialize task status badges
    initializeStatusBadges();
    
    // Initialize form validation
    initializeFormValidation();
    
    // Initialize task filtering
    initializeTaskFiltering();
});

// Function to initialize date pickers
function initializeDatePickers() {
    const dateInputs = document.querySelectorAll('input[type="date"]');
    dateInputs.forEach(input => {
        // Set min date to today for due dates
        if (input.id === 'dueDate' && !input.value) {
            const today = new Date().toISOString().split('T')[0];
            input.setAttribute('min', today);
        }
    });
}

// Function to initialize alert dismissal
function initializeAlertDismissal() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        // Add close button to alerts
        const closeButton = document.createElement('button');
        closeButton.type = 'button';
        closeButton.className = 'close';
        closeButton.innerHTML = '&times;';
        closeButton.style.float = 'right';
        closeButton.style.cursor = 'pointer';
        closeButton.style.border = 'none';
        closeButton.style.background = 'none';
        closeButton.style.fontSize = '1.25rem';
        closeButton.style.fontWeight = 'bold';
        closeButton.style.lineHeight = '1';
        
        closeButton.addEventListener('click', function() {
            alert.style.display = 'none';
        });
        
        alert.insertBefore(closeButton, alert.firstChild);
        
        // Auto-dismiss success alerts after 5 seconds
        if (alert.classList.contains('alert-success')) {
            setTimeout(() => {
                alert.style.display = 'none';
            }, 5000);
        }
    });
}

// Function to initialize status badges
function initializeStatusBadges() {
    const statusBadges = document.querySelectorAll('.badge[data-status]');
    statusBadges.forEach(badge => {
        const status = badge.getAttribute('data-status');
        
        switch (status) {
            case 'Pending':
                badge.classList.add('status-pending');
                break;
            case 'In Progress':
                badge.classList.add('status-in-progress');
                break;
            case 'Completed':
                badge.classList.add('status-completed');
                break;
        }
    });
}

// Function to initialize form validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;
            
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    isValid = false;
                    field.classList.add('is-invalid');
                    
                    // Add error message if not already present
                    let errorMessage = field.nextElementSibling;
                    if (!errorMessage || !errorMessage.classList.contains('invalid-feedback')) {
                        errorMessage = document.createElement('div');
                        errorMessage.className = 'invalid-feedback';
                        errorMessage.textContent = 'This field is required';
                        field.parentNode.insertBefore(errorMessage, field.nextSibling);
                    }
                } else {
                    field.classList.remove('is-invalid');
                    
                    // Remove error message if present
                    const errorMessage = field.nextElementSibling;
                    if (errorMessage && errorMessage.classList.contains('invalid-feedback')) {
                        errorMessage.remove();
                    }
                }
            });
            
            if (!isValid) {
                event.preventDefault();
            }
        });
    });
}

// Function to initialize task filtering
function initializeTaskFiltering() {
    const statusFilter = document.getElementById('statusFilter');
    const projectFilter = document.getElementById('projectFilter');
    
    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            applyFilters();
        });
    }
    
    if (projectFilter) {
        projectFilter.addEventListener('change', function() {
            applyFilters();
        });
    }
}

// Function to apply filters
function applyFilters() {
    const statusFilter = document.getElementById('statusFilter');
    const projectFilter = document.getElementById('projectFilter');
    
    let url = window.location.pathname;
    let params = [];
    
    if (statusFilter && statusFilter.value) {
        params.push('status=' + encodeURIComponent(statusFilter.value));
    }
    
    if (projectFilter && projectFilter.value) {
        params.push('project=' + encodeURIComponent(projectFilter.value));
    }
    
    if (params.length > 0) {
        url += '?' + params.join('&');
    }
    
    window.location.href = url;
}

// Function to confirm deletion
function confirmDelete(event, itemType) {
    if (!confirm(`Are you sure you want to delete this ${itemType}?`)) {
        event.preventDefault();
    }
}

// Function to toggle password visibility
function togglePasswordVisibility(inputId) {
    const passwordInput = document.getElementById(inputId);
    const toggleIcon = document.querySelector(`[data-toggle="${inputId}"]`);
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleIcon.textContent = '👁️';
    } else {
        passwordInput.type = 'password';
        toggleIcon.textContent = '👁️‍🗨️';
    }
}
