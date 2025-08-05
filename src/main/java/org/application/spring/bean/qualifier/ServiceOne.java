package org.application.spring.bean.qualifier;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component("serviceOne")
public class ServiceOne implements IService{

    public ServiceOne() {
        System.out.println("ServiceOne created!");
    }

    @Override
    public String print() {
        return "serviceOne";
    }
}
