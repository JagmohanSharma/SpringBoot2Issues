package com.example.springboot2issue.config.security.iam;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IamAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_BEARER = "Bearer";

    private AuthenticationManager authenticationManager;

    public IamAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isAuthenticated = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            isAuthenticated = authentication.isAuthenticated();
        }

        if (!isAuthenticated) {
            String bearerToken = extractBearerToken(request);

            if (org.apache.commons.lang.StringUtils.isNotBlank(bearerToken)) {
                Authentication authenticate = null;
                IamAuthToken iamAuthToken = new IamAuthToken(null, bearerToken, null);

                try {
                   authenticate  = authenticationManager.authenticate(iamAuthToken);
                } catch (AuthenticationException e) {

                }
                SecurityContextHolder.getContext().setAuthentication(authenticate);
            }

        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authenticationToken = extractTokenFromHeader(request);
        if (StringUtils.isEmpty(authenticationToken)) {
            authenticationToken = extractTokenFromRequestParam(request);
        }
        return authenticationToken;
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String authenticationToken = null;
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(authorizationHeader) && authorizationHeader.contains(HEADER_BEARER)) {
            authenticationToken = authorizationHeader.replace(HEADER_BEARER, "").trim();
        }
        return authenticationToken;
    }

    private String extractTokenFromRequestParam(HttpServletRequest request) {
        return request.getParameter("access_token");
    }
}
