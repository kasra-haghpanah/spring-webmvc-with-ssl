package org.application.spring.bean.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("persian")
@Profile("fa")
public class Persian  implements Language{
    @Override
    public String print() {
        return "Persian!";
    }
}
