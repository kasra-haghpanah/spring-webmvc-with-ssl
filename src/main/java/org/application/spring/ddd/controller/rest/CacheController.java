package org.application.spring.ddd.controller.rest;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.application.spring.configuration.security.JwtUtil;
import org.application.spring.configuration.server.ServerUtil;
import org.application.spring.ddd.dto.ProductDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CacheController {

    @RequestMapping(value = "/cache/example/id/{id}/key/{key}", method = RequestMethod.GET)
    @ResponseBody
    @Cacheable(
            cacheNames = "products",
            cacheResolver = "productCacheResolver",
            value = "products",
            key = "{#id, #key}",
            // condition = "#result != null and #result.userName() != null"و
            unless = "#result == null || #result.userName() == null"
    )
    public ProductDTO getProductById(
            @PathVariable("id") Long id,
            @PathVariable("key") String key,
            HttpServletRequest request) {
        String token = ServerUtil.getToken(request);
        Claims claims = JwtUtil.extractAllClaims(token);
        String userName = (String) claims.get("sub");

        simulateSlowService(); // فرضاً یک عملیات زمان‌بر
        ProductDTO result = new ProductDTO(id, key, userName);
        return result;
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(3000); // تأخیر ۳ ثانیه‌ای برای شبیه‌سازی عملیات سنگین
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
