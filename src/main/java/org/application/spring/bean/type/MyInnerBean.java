package org.application.spring.bean.type;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class MyInnerBean {

    @PostConstruct
    public void post() {
        System.out.println("post: myInnerBean -> " + hashCode());
    }

    @PreDestroy
    public void destroy() {
        System.out.println("destroy: myInnerBean -> " + hashCode());
    }

    public MyInnerBean() {
        System.out.println("myInnerBean created");
    }
}
