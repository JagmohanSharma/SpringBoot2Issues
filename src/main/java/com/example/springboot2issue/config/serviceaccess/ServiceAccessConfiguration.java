package com.example.springboot2issue.config.serviceaccess;

import com.example.springboot2issue.config.serviceaccess.vo.RestTemplateProperties;
import org.apache.http.conn.DnsResolver;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class ServiceAccessConfiguration {

    @Value("#{'${openplatform.service.access.internal.domains}'.split(',')}")
    private Set<String> internalDomains;

    @Bean
    @ConfigurationProperties(prefix = "openplatform.service.access.default")
    RestTemplateProperties defaultOpenPlatformServicesProperties() {
        return new RestTemplateProperties();
    }

    @Bean
    DnsResolver httpClientDnsResolver() {
        return new TimingDnsResolver(new SystemDefaultDnsResolver());
    }

    @Bean
    public ClientHttpRequestFactoryCreator clientHttpRequestFactoryCreator() {
        return new HttpComponentsClientHttpRequestFactoryCreator(httpClientDnsResolver(), defaultOpenPlatformServicesProperties(), this.internalDomains);
    }
}
