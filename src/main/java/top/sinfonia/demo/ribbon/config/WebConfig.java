package top.sinfonia.demo.ribbon.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * <br>
 * <b>Project:</b> spring-cloud-ribbon-demo<br>
 * <b>Date:</b> 2017/11/1 14:16<br>
 * <b>Author:</b> Asher<br>
 */
@Configuration
public class WebConfig {
    @Primary
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @LoadBalanced
    @Bean
    public RestTemplate loadBalanced() {
        return new RestTemplate();
    }
}
