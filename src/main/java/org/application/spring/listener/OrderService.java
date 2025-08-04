package org.application.spring.listener;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void completeOrder(int orderId) {
        // پردازش سفارش...
        eventPublisher.publishEvent(new OrderCompletedEvent(this, orderId));
    }
}
