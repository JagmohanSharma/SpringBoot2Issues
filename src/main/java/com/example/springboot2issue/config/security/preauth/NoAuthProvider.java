package com.example.springboot2issue.config.security.preauth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class NoAuthProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(
                authentication.getPrincipal(), authentication.getCredentials());
        result.setDetails(authentication.getDetails());
        return result;
    }

    /**
     * Indicate that this provider only supports PreAuthenticatedAuthenticationToken
     * (sub)classes.
     */
    public final boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
