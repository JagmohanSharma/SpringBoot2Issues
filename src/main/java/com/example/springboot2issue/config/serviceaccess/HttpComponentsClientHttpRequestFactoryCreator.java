package com.example.springboot2issue.config.serviceaccess;

import com.example.springboot2issue.config.serviceaccess.rest.basicauth.BasicAuthHttpComponentsClientHttpRequestFactory;
import com.example.springboot2issue.config.serviceaccess.vo.RestTemplateProperties;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class HttpComponentsClientHttpRequestFactoryCreator implements ClientHttpRequestFactoryCreator {
    private static final List<String> GET_METHODS = Collections.unmodifiableList(Arrays.asList("GET", "HEAD", "OPTIONS", "TRACE"));
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpComponentsClientHttpRequestFactoryCreator.class);
    private DnsResolver dnsResolver;
    private RestTemplateProperties defaultProperties;
    private Set<String> internalDomains;

    public HttpComponentsClientHttpRequestFactoryCreator(DnsResolver dnsResolver, RestTemplateProperties defaultProperties, Set<String> internalDomains) {
        this.dnsResolver = dnsResolver;
        this.defaultProperties = defaultProperties;
        this.internalDomains = internalDomains;
    }


    @Override
    public ClientHttpRequestFactory create(RestTemplateProperties properties) {
        HttpClient cookieDisabledHttpClient = getCookieDisabledHttpClient(properties);
        String username = getUsername(properties);
        String password = getPassword(properties);

        return new BasicAuthHttpComponentsClientHttpRequestFactory(cookieDisabledHttpClient,
                username, password);
    }


    private HttpClient getCookieDisabledHttpClient(RestTemplateProperties openPlatformHttpClientProperties) {
        RequestConfig config = buildHttpRequestConfig(openPlatformHttpClientProperties);
        int maxConnections = getMaxConnections(openPlatformHttpClientProperties);
        long connectionTimeToLive = getConnectionTimeToLive(openPlatformHttpClientProperties);
        long connectionIdleTimeout = getConnectionIdleTimeout(openPlatformHttpClientProperties);
        String userAgent = getUserAgent(openPlatformHttpClientProperties);

//        HttpRequestRetryHandler retryHandler = buildRetryHandler(openPlatformHttpClientProperties);

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setMaxConnTotal(maxConnections)
                .setMaxConnPerRoute(maxConnections)
                .setConnectionTimeToLive(connectionTimeToLive, TimeUnit.MILLISECONDS)
                .disableCookieManagement()
                .setDefaultRequestConfig(config)
                .setUserAgent(userAgent)
                .evictExpiredConnections()
//                .setRetryHandler(retryHandler)
                .setDnsResolver(dnsResolver);
        if (connectionIdleTimeout > 0) {
            httpClientBuilder.evictIdleConnections(connectionIdleTimeout, TimeUnit.MILLISECONDS);
        }
        return httpClientBuilder.build();
    }

//    private HttpRequestRetryHandler buildRetryHandler(
//            RestTemplateProperties openPlatformHttpClientProperties) {
//        int numStaleConnectionRetries = getMaxConnections(openPlatformHttpClientProperties) + 1;
//        int forceRetryAttempts = getForceRetryAttempts(openPlatformHttpClientProperties);
//
//        HttpRequestRetryHandler defaultRetryHandler = DefaultHttpRequestRetryHandler.INSTANCE;
//        if (forceRetryAttempts > 0) {
//            List<String> emptyMethodList = Collections.emptyList();
//            Map<Class<? extends IOException>, List<String>> forcedRetryClassMethodMap = new HashMap<>();
//            forcedRetryClassMethodMap.put(ConnectTimeoutException.class, emptyMethodList);
//            forcedRetryClassMethodMap.put(SocketTimeoutException.class, GET_METHODS);
//            forcedRetryClassMethodMap.put(SocketException.class, GET_METHODS);
//            defaultRetryHandler = new ForcedRetryByClassMethodRetryHandlerDecorator(defaultRetryHandler, forceRetryAttempts, forcedRetryClassMethodMap);
//        }
//        return new StaleConnectionAwareRetryHandlerDecorator(numStaleConnectionRetries, defaultRetryHandler);
//    }

    private RequestConfig buildHttpRequestConfig(RestTemplateProperties openPlatformHttpClientProperties) {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        int readTimeout = getReadTimeout(openPlatformHttpClientProperties);
        int connectionRequestTimeout = getConnectionRequestTimeout(openPlatformHttpClientProperties);
        int connectTimeout = getConnectTimeout(openPlatformHttpClientProperties);
        requestConfigBuilder.setCookieSpec(CookieSpecs.IGNORE_COOKIES);
        requestConfigBuilder.setSocketTimeout(readTimeout);
        requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
        requestConfigBuilder.setConnectTimeout(connectTimeout);
        return requestConfigBuilder.build();
    }

    private String getUsername(RestTemplateProperties properties) {
        if (properties.getSecurity() != null && properties.getSecurity().getBasic() != null && properties.getSecurity().getBasic().getUsername() != null) {
            return properties.getSecurity().getBasic().getUsername();
        } else {
            String hostname = properties.getHostname();
            if (isInternalRequest(hostname))
                return defaultProperties.getSecurity().getBasic().getUsername();
            else {
                LOGGER.error("Please provide username for rest client basic authentication for host {}", hostname);
                return null;
            }
        }
    }

    private String getPassword(RestTemplateProperties properties) {
        if (properties.getSecurity() != null && properties.getSecurity().getBasic() != null && properties.getSecurity().getBasic().getPassword() != null) {
            return properties.getSecurity().getBasic().getPassword();
        } else {
            String hostname = properties.getHostname();
            if (isInternalRequest(hostname))
                return defaultProperties.getSecurity().getBasic().getPassword();
            else {
                LOGGER.error("Please provide password for rest client basic authentication for host {}", hostname);
                return null;
            }
        }
    }

    private Integer getReadTimeout(RestTemplateProperties properties) {
        if (properties.getTimeout() != null && properties.getTimeout().getRead() != null) {
            return properties.getTimeout().getRead();
        } else {
            return defaultProperties.getTimeout().getRead();
        }
    }

    private Integer getForceRetryAttempts(RestTemplateProperties properties) {
        if (properties.getForceRetryAttempts() != null) {
            return properties.getForceRetryAttempts();
        } else {
            return defaultProperties.getForceRetryAttempts();
        }
    }

    private Integer getMaxConnections(RestTemplateProperties properties) {
        if (properties.getMaxConnections() != null) {
            return properties.getMaxConnections();
        } else {
            return defaultProperties.getMaxConnections();
        }
    }

    private String getUserAgent(RestTemplateProperties properties) {
        if (properties.getUserAgent() != null) {
            return properties.getUserAgent();
        } else {
            return defaultProperties.getUserAgent();
        }
    }

    private Integer getConnectionRequestTimeout(RestTemplateProperties properties) {
        if (properties.getTimeout() != null && properties.getTimeout().getConnectionRequest() != null) {
            return properties.getTimeout().getConnectionRequest();
        } else {
            return defaultProperties.getTimeout().getConnectionRequest();
        }
    }

    private Integer getConnectTimeout(RestTemplateProperties properties) {
        if (properties.getTimeout() != null && properties.getTimeout().getConnect() != null) {
            return properties.getTimeout().getConnect();
        } else {
            return defaultProperties.getTimeout().getConnect();
        }
    }

    private Long getConnectionTimeToLive(RestTemplateProperties properties) {
        if (properties.getTimeout() != null && properties.getTimeout().getConnectionTimeToLive() != null) {
            return properties.getTimeout().getConnectionTimeToLive();
        } else {
            return defaultProperties.getTimeout().getConnectionTimeToLive();
        }
    }

    private Long getConnectionIdleTimeout(RestTemplateProperties properties) {
        if (properties.getTimeout() != null && properties.getTimeout().getConnectionIdle() != null) {
            return properties.getTimeout().getConnectionIdle();
        } else {
            return defaultProperties.getTimeout().getConnectionIdle();
        }
    }

    private boolean isInternalRequest(String hostname) {
        for (String internalDomain : internalDomains) {
            if (hostname.contains(internalDomain)) {
                return true;
            }
        }
        return false;
    }
}
