package org.application.spring.ddd.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {


    @RequestMapping(value = "/authors", method = RequestMethod.GET)
    @ResponseBody
    public String getAuthors() {

        //userRepository.findAll();
        //model.addAttribute("authors", authorRepository.findAll());
        return "salam doustan";

    }


}
