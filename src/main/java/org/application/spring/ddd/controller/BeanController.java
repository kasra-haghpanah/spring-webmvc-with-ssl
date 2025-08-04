package org.application.spring.ddd.controller;

import org.application.spring.bean.type.MyBean;
import org.application.spring.bean.type.MyBeanWithLookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BeanController {

    final MyBean myBean;
    final MyBeanWithLookup myBeanWithLookup;

    public BeanController(MyBean myBean, MyBeanWithLookup myBeanWithLookup) {
        this.myBean = myBean;
        this.myBeanWithLookup = myBeanWithLookup;
    }

    @RequestMapping(value = "/make/mybean", method = RequestMethod.GET)
    @ResponseBody
    public String makeMyBean() {
        this.myBean.print();
        return "salam doustan";
    }

    @RequestMapping(value = "/make/mybean/with/lookup", method = RequestMethod.GET)
    @ResponseBody
    public String makeMyBeanWithLookup() {
        this.myBeanWithLookup.print();
        return "salam doustan";
    }


}
