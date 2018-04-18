package com.example.springboot2issue.config;

import com.example.springboot2issue.contentnegotiation.CustomContentNegotiationStrategy;
import com.example.springboot2issue.contentnegotiation.HandlerMappingsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

//@Configuration
public class ContentNegotiationConfig implements WebMvcConfigurer {

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    @Resource
    private ContentNegotiationProperties contentNegotiationProperties;

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        List<ContentNegotiationStrategy> strategies = new ArrayList<>();
        strategies.add(new CustomContentNegotiationStrategy(DEFAULT_MEDIA_TYPE, handlerMappingsProvider()));
        configurer.favorPathExtension(this.contentNegotiationProperties.isFavorPathExtension())
                .favorParameter(this.contentNegotiationProperties.isFavorParameter())
                .parameterName("format")
                .ignoreAcceptHeader(this.contentNegotiationProperties.isIgnoreAcceptHeader())
                .ignoreUnknownPathExtensions(this.contentNegotiationProperties.isIgnoreUnknownPathExtensions())
                .useRegisteredExtensionsOnly(!this.contentNegotiationProperties.isUseJaf())
                .strategies(strategies);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(contentNegotiationProperties.isUseSuffixPatternMatch());
    }

    @Bean
    public HandlerMappingsProvider handlerMappingsProvider() {
        return new HandlerMappingsProvider(DEFAULT_MEDIA_TYPE);
    }
}
