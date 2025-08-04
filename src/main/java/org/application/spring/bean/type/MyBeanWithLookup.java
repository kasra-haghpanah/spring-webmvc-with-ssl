package org.application.spring.bean.type;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Lookup;

public abstract class MyBeanWithLookup {

    @PostConstruct
    public void post() {
        System.out.println("post: myBeanWithLookup -> " + hashCode());
    }

    @PreDestroy
    public void destroy() {
        System.out.println("destroy: myBeanWithLookup -> " + hashCode());
    }


    public MyBeanWithLookup() {
        System.out.println("myBeanWithLookup created");
    }

    //@Lookup
    public abstract MyInnerBean getMyInnerBean();// method injection

    public void print() {
        System.out.println("myBeanWithLookup is printing!");
        MyInnerBean myInnerBean = getMyInnerBean();
        myInnerBean.destroy();

    }
}
