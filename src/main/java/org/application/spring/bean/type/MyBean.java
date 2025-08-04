package org.application.spring.bean.type;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class MyBean {

    final MyInnerBean myInnerBean;

    @PostConstruct
    public void post() {
        System.out.println("post: myBean -> " + hashCode());
    }

    @PreDestroy
    public void destroy() {
        System.out.println("destroy: myBean -> " + hashCode());
    }


    public MyBean(MyInnerBean myInnerBean) {
        this.myInnerBean = myInnerBean;
        System.out.println("mybean created");
    }

    public void print(){
        System.out.println("mybean is printing!");
    }
}
