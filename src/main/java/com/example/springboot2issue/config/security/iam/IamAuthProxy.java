package com.example.springboot2issue.config.security.iam;

import com.example.springboot2issue.config.serviceaccess.vo.RestTemplateProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

public class IamAuthProxy {

    @Resource
    private RestTemplate iamRestTemplate;

    @Resource
    private RestTemplateProperties iamRestTemplateConf;

    private String tokenValidateUrl;

    @PostConstruct
    public void init() {
        String url = iamRestTemplateConf.getUrl();
        String uri = "/identityservice/1.1.1/rest/oauth/validate/";
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        if (!uri.endsWith("/")) {
            uri += "/";
        }
        tokenValidateUrl = url + uri;
    }

    @Cacheable("addresses")
    public String validateUserFromToken(String token) {
        String iamUrl = tokenValidateUrl + token;
        IamOauthResponse forObject = iamRestTemplate.getForObject(iamUrl, IamOauthResponse.class);

        return forObject.getUserName();
    }
}
