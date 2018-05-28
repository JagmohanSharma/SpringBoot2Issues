package com.example.springboot2issue.config.serviceaccess.rest.basicauth;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

public class BasicAuthHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    private String username;

    private String password;

    public BasicAuthHttpComponentsClientHttpRequestFactory(String username, String password) {
        super();
        initialize(username, password);
    }

    public BasicAuthHttpComponentsClientHttpRequestFactory(HttpClient httpClient, String username, String password) {
        super(httpClient);
        initialize(username, password);
    }

    private void initialize(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        authCache.put(targetHost, basicAuth);

        HttpClientContext localcontext = HttpClientContext.create();
        localcontext.setAuthCache(authCache);

        if (username != null) {
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            localcontext.setCredentialsProvider(credsProvider);
        }
        return localcontext;
    }

}
