package org.application.spring.bean.conditional;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
        name = "make.conditional-bean",
        havingValue = "true"
)
@Component("conditionalBean")
public class ConditionalBean {

    public ConditionalBean() {
        System.out.println("ConditionalBean was created!");
    }
}
