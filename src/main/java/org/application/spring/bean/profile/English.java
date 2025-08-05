package org.application.spring.bean.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("english")
@Profile({"en", "default"})
public class English implements Language {
    @Override
    public String print() {
        return "English!";
    }
}
