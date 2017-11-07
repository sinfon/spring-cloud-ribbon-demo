package top.sinfonia.demo.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <br>
 * <b>Project:</b> spring-cloud-ribbon-demo<br>
 * <b>Date:</b> 2017/11/7 10:54<br>
 * <b>Author:</b> Asher<br>
 */
@Configuration
public class DemoserviceConfig {
    @Bean
    public IRule ribbonRule() {
        return new ZoneAvoidanceRule();
    }
}
