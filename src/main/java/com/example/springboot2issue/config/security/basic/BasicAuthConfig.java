package com.example.springboot2issue.config.security.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class BasicAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder =
                PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
                .withUser(securityProperties.getUser().getName()).password(passwordEncoder.encode(securityProperties.getUser().getPassword()))
                .roles(securityProperties.getUser().getRoles().toArray(new String[0]));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.httpBasic().authenticationEntryPoint(basicAuthEntryPoint());
        http.exceptionHandling().authenticationEntryPoint(basicAuthEntryPoint());
        http.cors().disable();
        http.csrf().disable();

        http.sessionManagement().maximumSessions(1);

//        http.rememberMe().alwaysRemember(true);

        http
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to("/health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).authenticated();
    }

    private AuthenticationEntryPoint basicAuthEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("SpringBoot2Issue");
        return entryPoint;
    }
}
