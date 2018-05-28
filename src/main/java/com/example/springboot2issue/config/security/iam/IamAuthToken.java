package com.example.springboot2issue.config.security.iam;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class IamAuthToken extends AbstractAuthenticationToken{

    private String token;

    private String userName;

    public IamAuthToken(Collection<? extends GrantedAuthority> authorities, String token, String userName) {
        super(authorities);
        this.token = token;
        this.userName = userName;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
