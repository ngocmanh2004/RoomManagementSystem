package com.techroom.roommanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("========== JWT FILTER START ==========");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Request Method: " + request.getMethod());
        
        String path = request.getRequestURI();
        
        if (path.startsWith("/api/auth/")) {
            System.out.println("⏭️ Skipping JWT filter for auth endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + (authHeader != null ? "EXISTS" : "MISSING"));
        
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Token extracted (first 30 chars): " + token.substring(0, Math.min(30, token.length())) + "...");
            
            try {
                username = jwtTokenProvider.extractUsername(token);
                System.out.println("✅ Username extracted: " + username);
            } catch (Exception e) {
                System.out.println("❌ Cannot extract username: " + e.getMessage());
                logger.warn("Cannot extract username from token: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ No Bearer token found in Authorization header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("🔍 Authenticating user: " + username);
            
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("✅ UserDetails loaded: " + userDetails.getUsername());
                System.out.println("✅ Authorities: " + userDetails.getAuthorities());

                if (jwtTokenProvider.validateToken(token, userDetails)) {
                    System.out.println("✅ Token is VALID");
                    
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ Authentication set in SecurityContext");
                    logger.debug("User " + username + " authenticated successfully");
                } else {
                    System.out.println("❌ Token validation FAILED");
                    logger.warn("Token validation failed for user: " + username);
                }
            } catch (Exception e) {
                System.out.println("❌ Error during authentication: " + e.getMessage());
                e.printStackTrace();
                logger.error("Error processing JWT token: " + e.getMessage(), e);
            }
        } else if (username != null) {
            System.out.println("ℹ️ User already authenticated in SecurityContext");
        } else {
            System.out.println("⚠️ No username extracted from token");
        }

        System.out.println("========== JWT FILTER END ==========");
        filterChain.doFilter(request, response);
    }
}