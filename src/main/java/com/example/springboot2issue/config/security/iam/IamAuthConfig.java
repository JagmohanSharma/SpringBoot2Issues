package com.example.springboot2issue.config.security.iam;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableCaching
@Order(Ordered.LOWEST_PRECEDENCE - 101)
public class IamAuthConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        IamAuthFilter iamAuthFilter = new IamAuthFilter(authenticationManager());
        http = http
                .requestMatchers().antMatchers("/info")
                .and();

        http.cors().disable();

        http.csrf().disable();

        http.addFilterBefore(iamAuthFilter, BasicAuthenticationFilter.class);

        http.authorizeRequests().anyRequest().authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(iamAuthProvider());
    }

    public AuthenticationProvider iamAuthProvider() {
        return new IamAuthProvider(iamAuthProxy());
    }

    @Bean
    public IamAuthProxy iamAuthProxy() {
        return new IamAuthProxy();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("addresses");
    }
}
