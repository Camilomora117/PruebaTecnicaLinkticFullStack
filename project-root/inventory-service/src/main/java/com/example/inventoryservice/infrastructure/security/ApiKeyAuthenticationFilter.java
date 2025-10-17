package com.example.inventoryservice.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import com.example.inventoryservice.infrastructure.web.exception.ErrorResponse;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    
    @Value("${inventory.api-key}")
    private String expectedApiKey;
    
    private final ObjectMapper objectMapper;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/actuator");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String apiKey = request.getHeader("X-API-KEY");
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Missing X-API-KEY header for request: {}", request.getRequestURI());
            sendUnauthorizedResponse(response, "Missing X-API-KEY header");
            return;
        }
        
        if (!expectedApiKey.equals(apiKey)) {
            log.warn("Invalid X-API-KEY for request: {}", request.getRequestURI());
            sendUnauthorizedResponse(response, "Invalid X-API-KEY");
            return;
        }

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_API"));
        var auth = new UsernamePasswordAuthenticationToken("api-key-user", null, authorities);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        log.debug("Valid X-API-KEY provided for request: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(message)
                .path("") // No tenemos acceso directo al path aquí
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

