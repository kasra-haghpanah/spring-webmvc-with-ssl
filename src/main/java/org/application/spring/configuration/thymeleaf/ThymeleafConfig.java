package org.application.spring.configuration.thymeleaf;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.application.spring.configuration.exception.RequestLoggingInterceptor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.application.spring.ddd.controller")
public class ThymeleafConfig implements WebMvcConfigurer {

    public static final String[] resourceHandler;
    public static final String[] resourceLocations;
    private final RequestLoggingInterceptor interceptor;

    static {

        resourceHandler = new String[]{
                "/css/**",
                "/custom/**",
                "/fonts/**",
                "/images/**",
                "/js/**",
                "/lib/**",
                "/view/**",
                "/favicon.**"
        };

        resourceLocations = new String[]{
                "classpath:static/css/",
                "classpath:static/custom/",
                "classpath:static/fonts/",
                "classpath:static/js/",
                "classpath:static/lib/",
                "classpath:static/view/",
                "classpath:static/",
                "classpath:static/images/"
        };

    }

    public ThymeleafConfig(RequestLoggingInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // https://www.baeldung.com/cachable-static-assets-with-spring-mvc
        registry.addResourceHandler(resourceHandler)
                .addResourceLocations(resourceLocations)
                .setCacheControl(CacheControl.maxAge(360, TimeUnit.SECONDS));

        registry.addResourceHandler("/swagger-ui")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }


    // for logging
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }

    @Bean
    public ITemplateResolver thymeleafTemplateResolver(ApplicationContext applicationContext){
        final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML);

        resolver.setCacheable(false);
        //resolver.setCacheablePatterns(Set.of("/**"));

        resolver.setCheckExistence(true);
        return resolver;
    }


    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver thymeleafTemplateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver);
        return templateEngine;
    }

    @Bean("thymeleafViewResolver")
    public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setContentType("text/html; charset=UTF-8");
        //viewResolver.setOrder(1);
        return viewResolver;
    }

}
