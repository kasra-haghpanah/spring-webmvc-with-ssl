package org.application.spring.bean.profile;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("spanish")
@Profile("sp")
public class Spanish implements Language{
    @Override
    public String print() {
        return "Spanish!";
    }
}
