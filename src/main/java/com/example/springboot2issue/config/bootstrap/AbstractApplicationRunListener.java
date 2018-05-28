package com.example.springboot2issue.config.bootstrap;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Implements all methods so that derived classes can implement just the method they care about.
 */
public abstract class AbstractApplicationRunListener implements SpringApplicationRunListener {
    private SpringApplication application;

    private String[] args;

    public AbstractApplicationRunListener(SpringApplication application,  String[] args) {
        this.application = application;
        this.args = args;
    }

    public SpringApplication getApplication() {
        return application;
    }

    public String[] getArgs() {
        return args;
    }

    /**
     * Called immediately when the run method has first started. Can be used for very
     * early initialization.
     */
    @Override
    public void starting() {

    }

    /**
     * Called once the environment has been prepared, but before the
     * {@link org.springframework.context.ApplicationContext} has been created.
     *
     * @param environment the environment
     */
    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {

    }

    /**
     * Called once the {@link org.springframework.context.ApplicationContext} has been created and prepared, but
     * before sources have been loaded.
     *
     * @param context the application context
     */
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    /**
     * Called once the application context has been loaded but before it has been
     * refreshed.
     *
     * @param context the application context
     */
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    /**
     * The context has been refreshed and the application has started but
     * {@link CommandLineRunner CommandLineRunners} and {@link ApplicationRunner
     * ApplicationRunners} have not been called.
     * @param context the application context
     */
    @Override
    public void started(ConfigurableApplicationContext context) {

    }

    /**
     * Called immediately before the run method finishes.
     *
     * @param context   the application context
     */
    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    /**
     * Called when a failure occurs when running the application.
     * @param context the application context
     * @param exception
     */
    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }
}