package com.example.springboot2issue.contentnegotiation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

public class HandlerMappingsProvider implements BeanFactoryAware {

	private Map<RequestMappingInfo, List<MediaType>> requestMappingDefaultMediaType;

	// This is required to get the instance of RequestMappingHandlerMapping
	// as when the Configurer is initialized, the handler mappings are unavailable
	private ListableBeanFactory beanFactory;

	private MediaType ignoredMediaType;

	public HandlerMappingsProvider(MediaType ignoredMediaType) {
		this.ignoredMediaType = ignoredMediaType;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ListableBeanFactory) beanFactory;
	}

	@PostConstruct
	public void init() {
		requestMappingDefaultMediaType = new HashMap<>();
		Map<RequestMappingInfo, HandlerMethod> handlerMethodsMapping = extractHandlerMethods();
		Iterator<Map.Entry<RequestMappingInfo, HandlerMethod>> handlerIter = handlerMethodsMapping.entrySet().iterator();
		while (handlerIter.hasNext()) {
			Map.Entry<RequestMappingInfo, HandlerMethod> entry = handlerIter.next();
			MediaType extractedMediaType = extractDefaultMediaType(entry.getValue());
			if (extractedMediaType != null && !extractedMediaType.equals(ignoredMediaType)) {
				requestMappingDefaultMediaType.put(entry.getKey(), Arrays.asList(extractedMediaType));
			}			
		}
	}

	public List<MediaType> getRequestDefaultProduces(HttpServletRequest request) {
		Iterator<Map.Entry<RequestMappingInfo, List<MediaType>>> iter = requestMappingDefaultMediaType.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<RequestMappingInfo, List<MediaType>> entry = iter.next();
			if (matchesRequest(entry.getKey(), request)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private MediaType extractDefaultMediaType(HandlerMethod handlerMethod) {
		DefaultResponseMediaType defaultResponseMediaType = findDefaultResponseMediaTypeAnnotation(handlerMethod);
		if (defaultResponseMediaType != null) {
			String mediaTypeValue = defaultResponseMediaType.value();
			return MediaType.parseMediaType(mediaTypeValue);
		}
		return null;
	}

	private Map<RequestMappingInfo, HandlerMethod> extractHandlerMethods() {
		Map<String, RequestMappingHandlerMapping> beansOfType = beanFactory.getBeansOfType(RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> handlerMethodsMapping = new HashMap<>();
		Iterator<RequestMappingHandlerMapping> handlerMappingBeansIter = beansOfType.values().iterator();
		while (handlerMappingBeansIter.hasNext()) {
			RequestMappingHandlerMapping mappingBean = handlerMappingBeansIter.next();
			handlerMethodsMapping.putAll(mappingBean.getHandlerMethods());
		}
		return handlerMethodsMapping;
	}

	/*
	 * The default implementation for RequestMappingInfo.getMatchingCondition(request) cannot be used as 
	 * it verifies the produces attribute and thus, sends the entire functionality into a loop.
	 * @param requestMapping
	 * @param request
	 * @return
	 */
	private boolean matchesRequest(RequestMappingInfo requestMapping, HttpServletRequest request) {
		RequestMethodsRequestCondition methods = requestMapping.getMethodsCondition().getMatchingCondition(request);
		ParamsRequestCondition params = requestMapping.getParamsCondition().getMatchingCondition(request);
		HeadersRequestCondition headers = requestMapping.getHeadersCondition().getMatchingCondition(request);
		ConsumesRequestCondition consumes = requestMapping.getConsumesCondition().getMatchingCondition(request);

		if (methods == null || params == null || headers == null || consumes == null) {
			return false;
		}

		PatternsRequestCondition patterns = requestMapping.getPatternsCondition().getMatchingCondition(request);
		if (patterns == null) {
			return false;
		}
		return true;
	}

	private DefaultResponseMediaType findDefaultResponseMediaTypeAnnotation(HandlerMethod handlerMethod) {
		Method method = handlerMethod.getMethod();
		DefaultResponseMediaType methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, DefaultResponseMediaType.class);
		if (methodAnnotation == null) {
			return AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), DefaultResponseMediaType.class);
		}
		return methodAnnotation;
	}

}
