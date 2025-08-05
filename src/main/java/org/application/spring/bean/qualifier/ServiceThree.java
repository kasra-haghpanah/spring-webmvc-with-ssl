package org.application.spring.bean.qualifier;


import org.springframework.stereotype.Component;

@Component("serviceThree")
public class ServiceThree implements IService{

    public ServiceThree() {
        System.out.println("ServiceTwo created!");
    }

    @Override
    public String print() {
        return "serviceThree";
    }

}
