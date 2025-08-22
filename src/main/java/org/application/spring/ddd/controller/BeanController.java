package org.application.spring.ddd.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.application.spring.bean.profile.Language;
import org.application.spring.bean.qualifier.IService;
import org.application.spring.bean.type.MyBean;
import org.application.spring.bean.type.MyBeanWithLookup;
import org.application.spring.bean.xml.Apartment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BeanController {

    private final MyBean myBean;
    private final MyBeanWithLookup myBeanWithLookup;
    private final IService serviceOne;
    private final IService serviceTwo;
    private final Language language;
    private final Apartment apartment;
    private final Apartment apartmentAsConstructorInjection;

    public BeanController(
            MyBean myBean,
            MyBeanWithLookup myBeanWithLookup,
            IService serviceOne,
            @Qualifier("serviceTwo") IService serviceTwo,
            Language language,
            @Qualifier("apartmentAsSetterInjection") Apartment apartment,
            @Qualifier("apartmentAsConstructorInjection") Apartment apartmentAsConstructorInjection
    ) {
        this.myBean = myBean;
        this.myBeanWithLookup = myBeanWithLookup;
        this.serviceOne = serviceOne;
        this.serviceTwo = serviceTwo;
        this.language = language;
        this.apartment = apartment;
        this.apartmentAsConstructorInjection = apartmentAsConstructorInjection;
    }

    @RequestMapping(value = "/make/mybean", method = RequestMethod.GET)
    @ResponseBody
    public String makeMyBean() {
        this.myBean.print();
        return "Hi friends!";
    }

    @RequestMapping(value = "/make/mybean/with/lookup", method = RequestMethod.GET)
    @ResponseBody
    public String makeMyBeanWithLookup() {
        this.myBeanWithLookup.print();
        return "Hi friends!";
    }


    @RequestMapping(value = "/qualifier/sample", method = RequestMethod.GET)
    @ResponseBody
    public String qualifierSample() {
        return this.serviceOne.print() + " & " + this.serviceTwo.print();
    }

    @RequestMapping(value = "/profile/sample", method = RequestMethod.GET)
    @ResponseBody
    public String profileSample() {
        return this.language.print();
    }

    @RequestMapping(value = "/xml/bean/sample", method = RequestMethod.GET)
    @ResponseBody
    public String xmlBeanSample(HttpServletRequest request) {
        return this.apartment.getBlock().print() + " " + this.apartment.toString() + " " + this.apartmentAsConstructorInjection.toString();
    }


}
