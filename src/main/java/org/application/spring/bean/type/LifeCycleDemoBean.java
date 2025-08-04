package org.application.spring.bean.type;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.application.spring.ddd.controller.SampleController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("lifeCycleDemoBean")
public class LifeCycleDemoBean implements InitializingBean, DisposableBean, BeanNameAware, BeanFactoryAware, ApplicationContextAware {

    public LifeCycleDemoBean() {
        System.out.println("new LifeCycleDemoBean()");//1
    }

    @Override
    public void setBeanName(String s) {
        System.out.println("setBeanName( " + s + " )");//2
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        SampleController sampleController = (SampleController) beanFactory.getBean("sampleController");
        System.out.println("setBeanFactory(BeanFactory beanFactory)");//3
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        SampleController sampleController = (SampleController) applicationContext.getBean("sampleController");
        System.out.println("setApplicationContext( " + applicationContext.getApplicationName() + " )");//4
    }

    public void beforeInit() {
        System.out.println("beforeInit()");//5
    }

    @PostConstruct
    public void postConstruct() {
        // afler making the intended object alongside with inject all dependencies that exist in the class.
        System.out.println("postConstruct()");//6
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet()");//7
    }

    public void afterInit() {
        System.out.println("afterInit()");//8
    }

    public void run() {
        System.out.println("run");//9
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("preDestroy()");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy()");
    }


}

