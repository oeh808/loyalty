package io.brightskies.loyalty.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.key}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String headerName = "x-api-key";
        final String authHeader = request.getHeader(headerName);
        //API key validation
        if (authHeader == null || !authHeader.equals(validApiKey)) {
            filterChain.doFilter(request, response);
            return;
        }
        Authentication auth = new ApiKeyAuthentication(authHeader, AuthorityUtils.NO_AUTHORITIES);
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
