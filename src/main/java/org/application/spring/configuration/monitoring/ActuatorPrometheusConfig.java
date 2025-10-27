package org.application.spring.configuration.monitoring;




import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.micrometer.metrics.autoconfigure.export.prometheus.PrometheusProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ConditionalOnProperty(
        name = "monitoring.active",
        havingValue = "true"
)
@Configuration(proxyBeanMethods = false)
public class ActuatorPrometheusConfig {

    @Bean
    @Primary
    public WebEndpointProperties webEndpointProperties() {
        WebEndpointProperties properties = new WebEndpointProperties();
        properties.getExposure().getInclude().add("health");     //https://localhost:8443/spring/actuator/health
        properties.getExposure().getInclude().add("info");       // https://localhost:8443/spring/actuator/info
        properties.getExposure().getInclude().add("prometheus"); // https://localhost:8443/spring/actuator/prometheus
        return properties;
    }

    @Bean
    public PrometheusProperties prometheusProperties() {
        PrometheusProperties properties = new PrometheusProperties();
        properties.setEnabled(true);
        return properties;
    }
}


