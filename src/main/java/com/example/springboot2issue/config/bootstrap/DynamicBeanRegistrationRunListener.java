package com.example.springboot2issue.config.bootstrap;

import com.example.springboot2issue.config.dynamicconfiguration.DynamicBeanConfigInterceptor;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class used to load and register bean dynamically. Based on property value given for "propertyKey" being used here, configuration class can be loaded and register for each value of propertyKey dynamically.
 * Implementation of this class need to override "loadAndRegisterDynaClass" method.
 * Created by sharmaja on 28/02/18.
 */
public abstract class DynamicBeanRegistrationRunListener extends AbstractApplicationRunListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicBeanRegistrationRunListener.class);
    private static final String PRIMARY = ".primary";
    private final String propertyKey;
    private final String dynamicBeanConfigName;

    public DynamicBeanRegistrationRunListener(String dynamicBeanConfigName, String propertyKey, SpringApplication application, String[] args) {
        super(application, args);
        this.propertyKey = propertyKey;
        this.dynamicBeanConfigName = dynamicBeanConfigName;
    }

    /**
     * when this method is executed, we get all the property sources ordered based on their precedence which also includes external property sources as consulPropertySource {@link org.springframework.cloud.consul.config.ConsulPropertySource}.
     * This method is executed two times
     * 1) while preparing a SpringApplication (e.g. populating its Environment) in a separate bootstrap context.
     * 2) While loading actual application context
     *
     * As we require to register beans in actual application context but not in bootstrap context, so we must not define propertyKey in any bootstrap properties file. As this should be define in applicationConfigurationProperties like application.properties file.
     * @param context the application context
     */
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        ClassLoader defaultClassLoader = getClassLoader(context);
        ConfigurableEnvironment environment = context.getEnvironment();
        String keysProp = environment.getProperty(propertyKey);
        if (StringUtils.isNotEmpty(keysProp)) {

            String[] propertyKeys = getPropertyKeys(keysProp, context);
            loadAndRegisterDynaClass(propertyKeys, defaultClassLoader, context);
        }
    }

    /**
     * This method is responsible to load dynamic bean configuration class dynamically for each property keys provided and register in beanDefinitionRegistry provided if not registered yet.
     * @param propertyKeys keys for which we should load configuration class dynamically.
     * @param defaultClassLoader default class loader being used to load class.
     * @param context application context in which dynamic bean will be registered.
     */
    private void loadAndRegisterDynaClass(String[] propertyKeys, ClassLoader defaultClassLoader, ConfigurableApplicationContext context) {
        BeanDefinitionRegistry beanDefRegistry = (BeanDefinitionRegistry) context;
        DynamicBeanConfigInterceptor dynaConfigInterceptor = getDynamicBeanConfigInterceptor();
        LOGGER.info("Dynamic config keys found: {}", ArrayUtils.toString(propertyKeys));
        for (String propertyKey : propertyKeys) {
            boolean isPrimary = false;
            if (propertyKey != null) {
                //Below code will get property to define if beans with current key should be marked @Primary, incase we have multiple keys and need to resolve between dynamic beans of similar types.
                String property = context.getEnvironment().getProperty(propertyKey + PRIMARY);
                if ("true".equals(property)) {
                    isPrimary = true;
                }
            }
            Class<?> configClass = getConfigClassDef(propertyKey, isPrimary, dynaConfigInterceptor, defaultClassLoader);
            String dynamicConfigBeanName = String.format(dynamicBeanConfigName, propertyKey);
            boolean containsBeanDefinition = beanDefRegistry.containsBeanDefinition(dynamicConfigBeanName);
            if (!containsBeanDefinition) {
                registerBeanDef(beanDefRegistry, configClass, dynamicConfigBeanName);
                LOGGER.info("Registered Dynamic config Bean Config: {}", dynamicConfigBeanName);
            }
        }
    }

    /**
     * Implement this method to provide dynamic bean config interceptor.
     * @return
     */
    protected abstract DynamicBeanConfigInterceptor getDynamicBeanConfigInterceptor();

    /**
     * Implement this method to load configuration class dynamically.
     * @param propName
     * @param dynaConfigInterceptor
     * @param classLoader
     * @return
     */
    protected abstract Class<?> getConfigClassDef(String propName, boolean isPrimary, DynamicBeanConfigInterceptor dynaConfigInterceptor, ClassLoader classLoader);

    private ClassLoader getClassLoader(ConfigurableApplicationContext context) {
        ClassLoader defaultClassLoader = null;
        if (context instanceof AbstractApplicationContext) {
            defaultClassLoader = context.getClassLoader();
        } else {
            defaultClassLoader = ClassUtils.getDefaultClassLoader();
        }
        return defaultClassLoader;
    }

    private String[] getPropertyKeys(String keysProp, ConfigurableApplicationContext context) {
        String[] unparsedKeys = keysProp.split(",");
        return parseKeys(unparsedKeys, context);
    }

    private String[] parseKeys(String[] unparsedKeys, BeanFactory beanFactory) {
        List<String> parsedKeys = new ArrayList<>();
        for (String unparsedKey : unparsedKeys) {
            String finalKey = unparsedKey.trim();
            if (finalKey.startsWith("#{")) {
                finalKey = parseSpelKey(finalKey, beanFactory);
            }
            if (StringUtils.isNotBlank(finalKey)) {
                parsedKeys.add(finalKey);
            }
        }
        return parsedKeys.toArray(new String[0]);
    }

    private String parseSpelKey(String unparsedKey, BeanFactory beanFactory) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));
        ExpressionParser parser = new SpelExpressionParser();
        String s = unparsedKey.substring(0, unparsedKey.length() - 1).substring(2);
        Expression expression = parser.parseExpression(s);
        String value = expression.getValue(context, String.class);
        if (value != null) {
            return value.trim();
        }
        return null;
    }

    protected void registerBeanDef(BeanDefinitionRegistry beanDefRegistry, Class<?> configClass, String dynamicConfigBeanName) {
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(configClass);
        BeanDefinition beanDefinition = bdb.getBeanDefinition();
        beanDefRegistry.registerBeanDefinition(dynamicConfigBeanName, beanDefinition);
    }

}
