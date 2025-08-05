package org.application.spring.bean.qualifier;

import org.springframework.stereotype.Component;

@Component("serviceTwo")
public class ServiceTwo implements IService{

    public ServiceTwo() {
        System.out.println("ServiceTwo created!");
    }

    @Override
    public String print() {
        return "serviceTwo";
    }

}
