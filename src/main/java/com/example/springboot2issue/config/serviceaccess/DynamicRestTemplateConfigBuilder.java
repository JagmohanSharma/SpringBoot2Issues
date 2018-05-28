package com.example.springboot2issue.config.serviceaccess;

import com.example.springboot2issue.config.dynamicconfiguration.DynamicBeanConfigInterceptor;
import com.example.springboot2issue.config.dynamicconfiguration.DynamicConfigBaseBuilder;
import com.example.springboot2issue.config.serviceaccess.vo.RestTemplateProperties;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * This class is used to build Rest template Configuration class dynamically using Byte Buddy. We basically create below mentioned 3 bean methods here, and some bean method have parameters annotated with {@link org.springframework.beans.factory.annotation.Qualifier}
 * 1. openPlatformServicesProperties which is annotated with {@link org.springframework.boot.context.properties.ConfigurationProperties} and {@link org.springframework.context.annotation.Bean}
 * and does not have any method parameter.
 * 2. clientHttpRequestFactory which is annotated and {@link org.springframework.context.annotation.Bean}
 * and have ClientHttpRequestFactoryCreator and OpenPlatformServicesProperties as method parameters.
 * 3. RestTemplate which is annotated with {@link org.springframework.context.annotation.Bean} and have clientHttpRequestFactory as method parameter.
 *
 * Class structure would be like below if for example we have defined openplatform.service.access.keys.service=commonService
 *
 *
 @Configuration("")
 public class CommonServicesRestTemplateConfig {

 @Bean(
   name = {"commonServicesRestTemplateConf"},
    value = {},
    initMethod = "",
    destroyMethod = "(inferred)",
    autowire = Autowire.NO
    )
 @ConfigurationProperties(
    value = "",
    prefix = "commonServices",
    ignoreInvalidFields = false,
    ignoreNestedProperties = false,
    ignoreUnknownFields = true,
    exceptionIfInvalid = true
    )
 public OpenPlatformServicesProperties openPlatformServicesProperties() {
  return dynaConfigInterceptor$qqpfvs0.configOpenPlatformServicesProperties();
 }

 @Bean(
    name = {"commonServicesClientHttpRequestFactory"},
    value = {},
    initMethod = "",
    destroyMethod = "(inferred)",
    autowire = Autowire.NO
    )
 @RefreshScope(
    proxyMode = ScopedProxyMode.TARGET_CLASS
    )
 public ClientHttpRequestFactory clientHttpRequestFactory(@Qualifier("clientHttpRequestFactoryCreator") ClientHttpRequestFactoryCreator var1
 , @Qualifier("commonServicesRestTemplateConf") OpenPlatformServicesProperties var2) {
   return dynaConfigInterceptor$qqpfvs0.configClientHttpRequestFactory(var1, var2);
 }

 @Bean(
    name = {"commonServicesRestTemplate"},
    value = {},
    initMethod = "",
    destroyMethod = "(inferred)",
    autowire = Autowire.NO
    )
 public RestTemplate configRestTemplate(@Qualifier("commonServicesClientHttpRequestFactory") ClientHttpRequestFactory var1) {
   return dynaConfigInterceptor$qqpfvs0.configRestTemplate(var1);
  }

 public CommonServicesRestTemplateConfig() {
 }
 }
 * Created by sharmaja on 26/02/18.
 */
public class DynamicRestTemplateConfigBuilder extends DynamicConfigBaseBuilder {

    private static final String METHOD_OPEN_PLATFORM_SERVICES_PROPERTIES = "openPlatformServicesProperties";

    private static final String METHOD_CLIENT_HTTP_REQUEST_FACTORY = "clientHttpRequestFactory";

    private static final String METHOD_CLIENT_HTTP_REQUEST_FACTORY_PARAM_REQUEST_FACTORY_CREATOR = "clientHttpRequestFactoryCreator";

    private static final String METHOD_CONFIG_REST_TEMPLATE = "configRestTemplate";

    private static final String DYNA_REST_TEMPLATE_CONFIG_CLASS_NAME = "com.example.springboot2issue.config.dynaconfig.%sRestTemplateConfig";

    private static final String REST_TEMPLATE_PROPERTIES_BEAN_NAME = "%sRestTemplateConf";

    private static final String CLIENT_HTTP_REQUEST_FACTORY_BEAN_NAME = "%sClientHttpRequestFactory";

    private static final String REST_TEMPLATE_BEAN_NAME = "%sRestTemplate";

    public DynamicRestTemplateConfigBuilder(String propName, DynamicBeanConfigInterceptor dynaConfigInterceptor, ClassLoader classLoader) {
        super(propName, dynaConfigInterceptor, classLoader);
    }

    public Class<?> loadConfigClass() {
        String className = getClassName(DYNA_REST_TEMPLATE_CONFIG_CLASS_NAME);
        try {
            return getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            return new Builder()
                    .appendConfigurationProperties()
                    .appendClientHttpRequestFactory()
                    .appendRestTemplate()
                    .build();
        }
    }

    /**
     * This class is responsible to build and load configuration class dynamically using bytebuddy. This class that is created by Byte Buddy is emitted by an instance of the ByteBuddy class here.
     * A type that is created by Byte Buddy is represented by an instance of DynamicType.Unloaded. which is loaded into JVM usng provided class loader.
     * We are also defining required bean methods in Configuration class here.
     */
    private class Builder {

        private DynamicType.Builder<Object> restTemplateConfigBuilder;

        public Builder() {
            restTemplateConfigBuilder = init(DYNA_REST_TEMPLATE_CONFIG_CLASS_NAME);
        }

        /**
         * This method will append bean method for ConfigurationProperties.
         * @return
         */
        private Builder appendConfigurationProperties() {
            String restTemplatePropertiesBeanName = getFormattedBeanName(REST_TEMPLATE_PROPERTIES_BEAN_NAME);
            AnnotationDescription openPlatformServicesPropertiesBeanAnnotationDesc = buildBeanAnnotationDescription(restTemplatePropertiesBeanName);
            AnnotationDescription configurationPropertiesBeanAnnotationDesc = buildPropertiesConfigurationAnnotationDescription(getPropName());
            restTemplateConfigBuilder = restTemplateConfigBuilder
                    .defineMethod(METHOD_OPEN_PLATFORM_SERVICES_PROPERTIES, RestTemplateProperties.class, Visibility.PUBLIC)
                    .intercept(MethodDelegation.to(getDynaConfigInterceptor()))
                    .annotateMethod(openPlatformServicesPropertiesBeanAnnotationDesc, configurationPropertiesBeanAnnotationDesc);
            return this;
        }

        /**
         * This method will append bean method for ClientHttpRequestFactory. this also contains some method parameters which are annotated with {@link org.springframework.beans.factory.annotation.Qualifier}
         * @return
         */
        private Builder appendClientHttpRequestFactory() {
            String restTemplatePropertiesBeanName = getFormattedBeanName(REST_TEMPLATE_PROPERTIES_BEAN_NAME);
            String clientHttpRequestFactoryBeanName = getFormattedBeanName(CLIENT_HTTP_REQUEST_FACTORY_BEAN_NAME);
            AnnotationDescription requestFactoryCreatorQualifierAnnotationDesc = buildQualifierAnnotationDescription(METHOD_CLIENT_HTTP_REQUEST_FACTORY_PARAM_REQUEST_FACTORY_CREATOR);
            AnnotationDescription openPlatformServicesPropertiesQualifierAnnotationDesc = buildQualifierAnnotationDescription(restTemplatePropertiesBeanName);
//            AnnotationDescription refreshScopeAnnotationDesc = buildRefreshScopeAnnotationDescription();
            AnnotationDescription clientHttpRequestFactoryBeanAnnotationDesc = buildBeanAnnotationDescription(clientHttpRequestFactoryBeanName);
            restTemplateConfigBuilder = restTemplateConfigBuilder
                    .defineMethod(METHOD_CLIENT_HTTP_REQUEST_FACTORY, ClientHttpRequestFactory.class, Visibility.PUBLIC)
                    .withParameter(ClientHttpRequestFactoryCreator.class, METHOD_CLIENT_HTTP_REQUEST_FACTORY_PARAM_REQUEST_FACTORY_CREATOR)
                    .annotateParameter(requestFactoryCreatorQualifierAnnotationDesc)
                    .withParameter(RestTemplateProperties.class, METHOD_OPEN_PLATFORM_SERVICES_PROPERTIES)
                    .annotateParameter(openPlatformServicesPropertiesQualifierAnnotationDesc)
                    .intercept(MethodDelegation.to(getDynaConfigInterceptor()))
                    .annotateMethod(clientHttpRequestFactoryBeanAnnotationDesc);
            return this;
        }

        /**
         * This method will append bean method for rest template and this also contains method parameter annotated with {@link org.springframework.beans.factory.annotation.Qualifier}
         * @return
         */
        private Builder appendRestTemplate() {
            String clientHttpRequestFactoryBeanName = getFormattedBeanName(CLIENT_HTTP_REQUEST_FACTORY_BEAN_NAME);
            String restTemplateBeanName = getFormattedBeanName(REST_TEMPLATE_BEAN_NAME);
            AnnotationDescription clientHttpRequestFactoryQualifierAnnotationDesc = buildQualifierAnnotationDescription(clientHttpRequestFactoryBeanName);
            AnnotationDescription restTemplateBeanAnnotationDesc = buildBeanAnnotationDescription(restTemplateBeanName);
            restTemplateConfigBuilder = restTemplateConfigBuilder
                    .defineMethod(METHOD_CONFIG_REST_TEMPLATE, RestTemplate.class, Visibility.PUBLIC)
                    .withParameter(ClientHttpRequestFactory.class, METHOD_CLIENT_HTTP_REQUEST_FACTORY)
                    .annotateParameter(clientHttpRequestFactoryQualifierAnnotationDesc)
                    .intercept(MethodDelegation.to(getDynaConfigInterceptor()))
                    .annotateMethod(restTemplateBeanAnnotationDesc);
            return this;
        }

        /**
         * This method first create the type using byte buddy and then load into JVM using provided class loader.
         * @return
         */
        private Class<?> build() {
            return restTemplateConfigBuilder.make().load(getClassLoader()).getLoaded();
        }

    }

}
