package org.application.spring.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class ListenerConfig {

    @EventListener
    public void handleOrderCompleted(OrderCompletedEvent event) {
        System.out.println("OrderCompletedEvent (annotation-based)!");
        OrderService orderService = (OrderService) event.getSource();
        System.out.println("Received order: " + event.getOrderId());
    }

    @Bean("eventListener")
    public ApplicationListener<ContextRefreshedEvent> eventListener(){ // ==><==
        return new ApplicationListener<ContextRefreshedEvent>() {
            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) {
                var context = event.getApplicationContext();
            }
        };
    }


    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent event) { // ==><==
        System.out.println("Context refreshed (annotation-based)!");


        /*

ApplicationListener<ContextRefreshedEvent>
 یک رابط (interface) در اسپرینگ است
که به شما امکان می‌دهد رویدادهای خاصی را در چرخه حیات برنامه گوش دهید (listen کنید).
در این مورد خاص، وقتی رویداد ContextRefreshedEvent رخ می‌دهد
(یعنی وقتی ApplicationContext اسپرینگ به‌روزرسانی یا refresh می‌شود)،
 متد onApplicationEvent فراخوانی می‌شود.


 اجرای کد پس از کامل شدن راه‌اندازی ApplicationContext
مقداردهی اولیه داده‌ها پس از راه‌اندازی اسپرینگ
انجام تنظیمات نهایی قبل از شروع به کار برنامه
         */

        // می‌توانید به ApplicationContext دسترسی داشته باشید:
        var context = event.getApplicationContext();
    }

    @EventListener
    public void handleContextStarted(ContextStartedEvent event) {
        System.out.println("Context started (annotation)");
    }

    @EventListener
    public void handleAppReady(ApplicationReadyEvent event) {

//        ApplicationReadyEvent یکی از رویدادهای مهم در Spring Boot است که نشان‌دهنده آمادگی کامل برنامه برای سرویس‌دهی است. این رویداد پس از موارد زیر منتشر می‌شود:
//        راه‌اندازی کامل ApplicationContext
//        اجرای تمام CommandLineRunnerها و ApplicationRunnerها
//        آماده‌بودن تمام endpointهای وب (در برنامه‌های وب)

        System.out.println("App ready (annotation)");
    }

}
