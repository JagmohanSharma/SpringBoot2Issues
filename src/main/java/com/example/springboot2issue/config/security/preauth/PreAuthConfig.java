package com.example.springboot2issue.config.security.preauth;

import com.example.springboot2issue.config.security.iam.IamAuthFilter;
import com.example.springboot2issue.config.security.iam.IamAuthProvider;
import com.example.springboot2issue.config.security.iam.IamAuthProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER - 101)
@EnableConfigurationProperties({SecurityProperties.class, PreauthAuthenticationProperties.class})
public class PreAuthConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private PreauthAuthenticationProperties preauthAuthenticationProperties;

    @Autowired
    private IamAuthProxy iamAuthenticationProxy;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RequestHeaderAuthenticationFilter headerAuthenticationFilter = new RequestHeaderAuthenticationFilter();
        headerAuthenticationFilter.setExceptionIfHeaderMissing(false);
        headerAuthenticationFilter.setPrincipalRequestHeader("REMOTE_USER");
        headerAuthenticationFilter.setAuthenticationManager(authenticationManager());

        http.csrf().disable();
        http.cors().disable();
        http.addFilterBefore(headerAuthenticationFilter, BasicAuthenticationFilter.class);
        http.requestMatchers().antMatchers("/env");
        http.authorizeRequests().anyRequest().authenticated();
        if (preauthAuthenticationProperties.isSecondaryIamEnabled()) {
            IamAuthFilter iamTokenFilter = new IamAuthFilter(authenticationManager());
            http = http.addFilterAfter(iamTokenFilter, RequestHeaderAuthenticationFilter.class)
                    .authorizeRequests().anyRequest().authenticated()
                    .and();
        }
        if (preauthAuthenticationProperties.getFallback().isBasic()) {
            http = http.httpBasic().authenticationEntryPoint(basicAuthEntryPoint()).and();
        }
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(new NoAuthProvider());
        if (preauthAuthenticationProperties.isSecondaryIamEnabled()) {
            auth.authenticationProvider(iamAuthenticationProvider());
        }
        if (preauthAuthenticationProperties.getFallback().isBasic()) {
            PasswordEncoder passwordEncoder =
                    PasswordEncoderFactories.createDelegatingPasswordEncoder();
            auth.inMemoryAuthentication()
                    .withUser(securityProperties.getUser().getName())
                    .password(passwordEncoder.encode(securityProperties.getUser().getPassword()))
                    .roles(securityProperties.getUser().getRoles().toArray(new String[0]));
        }

    }

    private IamAuthProvider iamAuthenticationProvider() {
        IamAuthProvider provider = new IamAuthProvider(iamAuthenticationProxy);
        /*if (preAuthenticatedUserDetailsServiceProvider != null) {
            provider.setPreAuthenticatedUserDetailsService(preAuthenticatedUserDetailsServiceProvider.buildPreAuthenticatedUserDetailsService());
        }*/
        return provider;
    }

    private AuthenticationEntryPoint basicAuthEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("SpringBoot2Issue");
        return entryPoint;
    }}
