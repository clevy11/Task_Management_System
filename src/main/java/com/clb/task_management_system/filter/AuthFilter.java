package com.clb.task_management_system.filter;

import com.clb.task_management_system.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Remove the WebFilter annotation since we're using web.xml configuration
public class AuthFilter implements Filter {
    
    // List of paths that don't require authentication
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/auth", "/login", "/register", "/logout", "/css", "/js", "/images"
    );
    
    // List of paths that require admin role
    private static final List<String> ADMIN_PATHS = Arrays.asList(
            "/admin", "/users", "/project/create"
    );
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getServletPath();
        
        // Check if the requested path is public
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is authenticated
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth?action=showLogin");
            return;
        }
        
        // Check if the path requires admin role
        if (isAdminPath(path)) {
            User user = (User) session.getAttribute("user");
            if (!user.isAdmin()) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard?error=unauthorized");
                return;
            }
        }
        
        // Continue with the request
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Cleanup code if needed
    }
    
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
    
    private boolean isAdminPath(String path) {
        return ADMIN_PATHS.stream().anyMatch(path::startsWith);
    }
}
