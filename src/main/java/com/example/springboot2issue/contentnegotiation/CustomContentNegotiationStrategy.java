package com.example.springboot2issue.contentnegotiation;

import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class CustomContentNegotiationStrategy implements ContentNegotiationStrategy {

	private final ContentNegotiationStrategy defaultContentNegotiationStrategy;

	private final HandlerMappingsProvider handlerMappingsProvider;

	public CustomContentNegotiationStrategy(MediaType defaultMediaType, HandlerMappingsProvider handlerMappingsProvider) {
		this.defaultContentNegotiationStrategy = new FixedContentNegotiationStrategy(defaultMediaType);
		this.handlerMappingsProvider = handlerMappingsProvider;
	}

	@Override
	public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest) throws HttpMediaTypeNotAcceptableException {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		List<MediaType> requestDefaultProduces = handlerMappingsProvider.getRequestDefaultProduces(request);
		if (CollectionUtils.isEmpty(requestDefaultProduces)) {
			return defaultContentNegotiationStrategy.resolveMediaTypes(webRequest);
		}
		return requestDefaultProduces;
	}

}
