package com.example.springboot2issue.config.serviceaccess;

import com.example.springboot2issue.config.bootstrap.DynamicBeanRegistrationRunListener;
import com.example.springboot2issue.config.dynamicconfiguration.DynamicBeanConfigInterceptor;
import org.springframework.boot.SpringApplication;

/**
 * SpringApplicationRunListener that loads and register configuration class dynamically based on provided property value for "openplatform.service.access.keys.service".
 * This class register configuration class by annotating with {@link org.springframework.context.annotation.Configuration} so that this can be processed by {@link org.springframework.context.annotation.ConfigurationClassPostProcessor}
 * Loaded via META-INF/spring.factories
 * Created by sharmaja on 26/02/18.
 */
public class DynamicRestTemplateRegisteringRunListener extends DynamicBeanRegistrationRunListener {

    public static final String SERVICE_ACCESS_PROP_KEY = "openplatform.service.access.default.keys";

    private static final String DYNAMIC_REST_TEMPLATE_CONFIG_BEAN_NAME = "%sRestTemplateConfig";

    public DynamicRestTemplateRegisteringRunListener(SpringApplication application, String[] args) {
        super(DYNAMIC_REST_TEMPLATE_CONFIG_BEAN_NAME, SERVICE_ACCESS_PROP_KEY, application, args);
    }

    @Override
    protected DynamicBeanConfigInterceptor getDynamicBeanConfigInterceptor() {
        return new DynamicRestTemplateConfigInterceptor();
    }

    @Override
    protected Class<?> getConfigClassDef(String propName, boolean isPrimary, DynamicBeanConfigInterceptor dynaConfigInterceptor, ClassLoader classLoader) {
        DynamicRestTemplateConfigBuilder config = new DynamicRestTemplateConfigBuilder(propName, dynaConfigInterceptor, classLoader);
        return config.loadConfigClass();
    }
}
