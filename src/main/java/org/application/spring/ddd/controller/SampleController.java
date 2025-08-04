package org.application.spring.ddd.controller;


import org.application.spring.listener.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {

    final OrderService orderService;

    public SampleController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(value = "/orders/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public String getUserOrder(@PathVariable int orderId) {


        orderService.completeOrder(orderId);
        //userRepository.findAll();
        //model.addAttribute("authors", authorRepository.findAll());
        return "salam doustan";

    }


}
