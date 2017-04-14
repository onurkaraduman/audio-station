package com.onrkrdmn.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author Onur Karaduman
 * @since 02.04.17
 */
@Configuration
public class StaticApplicationContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * Note that this is a static method which expose ApplicationContext
     **/
    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static Object getBean(String beanName) {
        return getContext().getBean(beanName);
    }
}