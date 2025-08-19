package org.application.spring.ddd.controller;

import jakarta.validation.Valid;
import org.application.spring.ddd.dto.StoreDTO;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@Validated
public class ValidateController {


    @RequestMapping(value = "/validate/store", method = RequestMethod.POST)
    @ResponseBody
    public String createStore(@RequestBody @Valid StoreDTO storeDTO) {
        return "Store created successfully";
    }


}
