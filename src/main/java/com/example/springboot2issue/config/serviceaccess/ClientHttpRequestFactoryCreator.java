package com.example.springboot2issue.config.serviceaccess;

import com.example.springboot2issue.config.serviceaccess.vo.RestTemplateProperties;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * Class to abstract the creation of the ClientHttpRequestFactory that RestTemplate uses
 */
public interface ClientHttpRequestFactoryCreator {
    ClientHttpRequestFactory create(RestTemplateProperties properties);
}