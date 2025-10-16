package org.application.spring.ddd.controller.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.application.spring.configuration.server.ServerUtil;
import org.application.spring.ddd.dto.ProductDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;

@Controller
public class CacheController {

    @RequestMapping(value = "/cache/example/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Cacheable(
            cacheNames = "products",
            cacheResolver = "productCacheResolver",
            value = "products",
            key = "{#id}",
            condition = "#result.content() != null"
            //unless = "#result.content() == null"
    )
    public ProductDTO getProductById(@PathVariable Long id, HttpServletRequest request) {
        String authorization = ServerUtil.getAuthorization(request);
        simulateSlowService(); // فرضاً یک عملیات زمان‌بر
        return new ProductDTO(id, "Product " + id);
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(3000); // تأخیر ۳ ثانیه‌ای برای شبیه‌سازی عملیات سنگین
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
