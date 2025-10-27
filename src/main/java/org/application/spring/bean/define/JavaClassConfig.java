package org.application.spring.bean.define;

import org.application.spring.bean.type.MyBean;
import org.application.spring.bean.type.MyBeanWithLookup;
import org.application.spring.bean.type.MyInnerBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

@Configuration(proxyBeanMethods = false)
public class JavaClassConfig {


    @Bean("myBean")
    @DependsOn({"myInnerBean"})
    @Scope("singleton")
    public MyBean myBean(MyInnerBean myInnerBean) {
        return new MyBean(myInnerBean);
    }

    @Bean("myBeanWithLookup")
    public MyBeanWithLookup myBeanWithLookup(ApplicationContext applicationContext){
        return new MyBeanWithLookup() {
            @Override
            public MyInnerBean getMyInnerBean() {
                return applicationContext.getBean(MyInnerBean.class);
            }
        };
    }


}
