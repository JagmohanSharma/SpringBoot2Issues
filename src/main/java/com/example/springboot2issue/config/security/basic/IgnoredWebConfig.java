package com.example.springboot2issue.config.security.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties(ServerProperties.class)
@Order(SecurityProperties.IGNORED_ORDER)
public class IgnoredWebConfig implements WebSecurityConfigurer<WebSecurity> {

    private static List<String> DEFAULT_IGNORED = Arrays.asList("/css/**", "/js/**",
            "/images/**", "/webjars/**", "/**/favicon.ico", "/health");

    @Autowired
    private ServerProperties serverProperties;

    @Override
    public void init(WebSecurity builder) throws Exception {
        String[] paths = this.serverProperties.getServlet().getPathsArray(DEFAULT_IGNORED);
        List<RequestMatcher> matchers = new ArrayList<RequestMatcher>();
        if (!ObjectUtils.isEmpty(paths)) {
            for (String pattern : paths) {
                matchers.add(new AntPathRequestMatcher(pattern, null));
            }
        }
        if (!matchers.isEmpty()) {
            builder.ignoring().requestMatchers(new OrRequestMatcher(matchers));
        }

    }

    @Override
    public void configure(WebSecurity builder) throws Exception {

    }
}
