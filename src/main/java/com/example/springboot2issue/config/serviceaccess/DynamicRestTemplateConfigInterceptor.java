package com.example.springboot2issue.config.serviceaccess;

import com.example.springboot2issue.config.dynamicconfiguration.DynamicBeanConfigInterceptor;
import com.example.springboot2issue.config.serviceaccess.vo.RestTemplateProperties;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * This class is used in {@link DynamicRestTemplateConfigBuilder} and intercepts method calls and delegate to below methods.
 * ByteBuddy would auto-magically find appropriate method from this class, that matches the intercepted method signature and invokes it.
 * Byte Buddy does not require target methods to be named equally to a source method. In can of any ambiguity, Byte Buddy mimics the Java compiler by choosing the method binding with the most specific parameter types.
 * Created by sharmaja on 26/02/18.
 */
public class DynamicRestTemplateConfigInterceptor implements DynamicBeanConfigInterceptor {

    public RestTemplateProperties configOpenPlatformServicesProperties() {
        return new RestTemplateProperties();
    }

    public ClientHttpRequestFactory configClientHttpRequestFactory(ClientHttpRequestFactoryCreator clientHttpRequestFactoryCreator, RestTemplateProperties openPlatformServicesProperties) {
        return clientHttpRequestFactoryCreator.create(openPlatformServicesProperties);
    }

    public RestTemplate configRestTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        return restTemplate;
    }

}
