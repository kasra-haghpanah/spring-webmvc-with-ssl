package org.application.spring.ddd.controller.rest;


import jakarta.servlet.http.HttpServletRequest;
import org.application.spring.configuration.exception.ApplicationException;
import org.application.spring.listener.OrderService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Controller
public class SampleController {

    private final OrderService orderService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public SampleController(OrderService orderService, MessageSource messageSource, LocaleResolver localeResolver) {
        this.orderService = orderService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @RequestMapping(value = "/orders/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public String getUserOrder(@PathVariable int orderId) {
        orderService.completeOrder(orderId);
        return "salam doustan";
    }


    @RequestMapping(value = "/check/exception", method = RequestMethod.GET)
    @ResponseBody
    public String checkException(HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        if (1 == 1) {
            throw new ApplicationException("check.exception", HttpStatus.PAYMENT_REQUIRED, "test-one", 2);
        }
        return "salam doustan";
    }


}
