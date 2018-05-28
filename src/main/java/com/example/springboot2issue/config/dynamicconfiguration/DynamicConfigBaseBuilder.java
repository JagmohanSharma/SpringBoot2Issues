package com.example.springboot2issue.config.dynamicconfiguration;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * This class is a base class which is being used to build Classes dynamically using byte buddy.
 * Created by sharmaja on 27/02/18.
 */
public class DynamicConfigBaseBuilder {

    private final String propName;

    private final DynamicBeanConfigInterceptor dynaConfigInterceptor;

    private final ClassLoader classLoader;

    public DynamicConfigBaseBuilder(String propName, DynamicBeanConfigInterceptor dynaConfigInterceptor, ClassLoader classLoader) {
        this.propName = propName;
        this.dynaConfigInterceptor = dynaConfigInterceptor;
        this.classLoader = classLoader;
    }

    /**
     * This method will initialize the basic ByteBuddy dynamicType builder for particular config class which would have {@link Configuration} annotation.
     * In case we need to add any other specific class level annotation, that should be taken care at lower level.
     * @param dynaBeanConfigClassName class name which will be used to create dynamic config class using byte buddy.
     * @return DynamicType builder object
     */
    protected DynamicType.Builder<Object> init(String dynaBeanConfigClassName) {
        final String className = getClassName(dynaBeanConfigClassName);
        AnnotationDescription configurationAnnotationDesc = buildConfigurationAnnotationDescription();
        return new ByteBuddy()
                .with(new NamingStrategy.AbstractBase() {
                    @Override
                    protected String name(TypeDescription superClass) {
                        return className;
                    }
                })
                .subclass(Object.class)
                .annotateType(configurationAnnotationDesc);
    }

    protected AnnotationDescription buildConfigurationAnnotationDescription() {
        return AnnotationDescription.Builder
                .ofType(Configuration.class)
                .build();
    }

    protected AnnotationDescription buildQualifierAnnotationDescription(String value) {
        return AnnotationDescription.Builder
                .ofType(Qualifier.class)
                .define("value", value)
                .build();
    }

    protected AnnotationDescription buildBeanAnnotationDescription(String beanName) {
        return AnnotationDescription.Builder
                .ofType(Bean.class)
                .defineArray("name", new String[]{beanName})
                .build();
    }

    protected AnnotationDescription buildPropertiesConfigurationAnnotationDescription(String propertiesPrefix) {
        return AnnotationDescription.Builder
                .ofType(ConfigurationProperties.class)
                .define("prefix", propertiesPrefix.toLowerCase())
                .build();
    }

//    protected AnnotationDescription buildRefreshScopeAnnotationDescription() {
//        return AnnotationDescription.Builder
//                .ofType(RefreshScope.class)
//                .build();
//    }

    protected AnnotationDescription buildConditionalOnMissingBeanAnnotationDescription(String beanName) {
        return AnnotationDescription.Builder
                .ofType(ConditionalOnMissingBean.class)
                .defineArray("name", new String[]{beanName})
                .build();
    }

    protected AnnotationDescription buildAutoConfigureBeforeAnnotationDescription(Class<?> autoConfigClass) {
        return AnnotationDescription.Builder
                .ofType(AutoConfigureBefore.class)
                .defineTypeArray("value", new Class[]{autoConfigClass})
                .build();
    }

    protected AnnotationDescription buildPrimaryAnnotationDescription() {
        return AnnotationDescription.Builder
                .ofType(Primary.class)
                .build();
    }

    protected AnnotationDescription buildConditionalOnExpressionAnnotationDescription(String expression) {
        return AnnotationDescription.Builder
                .ofType(ConditionalOnExpression.class)
                .define("value", expression)
                .build();
    }

    protected String getFormattedBeanName(String unparsedBeanName) {
        return String.format(unparsedBeanName, propName);
    }

    protected String getClassName(String unparsedClassName) {
        String camelCasedPropName = propName.substring(0, 1).toUpperCase() + propName.substring(1);
        return String.format(unparsedClassName, camelCasedPropName);
    }

    public String getPropName() {
        return propName;
    }

    public DynamicBeanConfigInterceptor getDynaConfigInterceptor() {
        return dynaConfigInterceptor;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}