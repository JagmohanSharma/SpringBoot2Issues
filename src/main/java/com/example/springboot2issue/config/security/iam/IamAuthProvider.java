package com.example.springboot2issue.config.security.iam;

import com.example.springboot2issue.config.security.vo.AuthenticatedUser;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class IamAuthProvider implements AuthenticationProvider{

    private IamAuthProxy iamAuthProxy;

    public IamAuthProvider(IamAuthProxy iamAuthProxy) {
        this.iamAuthProxy = iamAuthProxy;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        IamAuthToken iamAuthToken = (IamAuthToken)authentication;

        String user = iamAuthProxy.validateUserFromToken((String) iamAuthToken.getCredentials());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(iamAuthToken.getAuthorities(), (String) iamAuthToken.getCredentials(), user);
        return authenticatedUser;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return IamAuthToken.class.isAssignableFrom(authentication);
    }
}
