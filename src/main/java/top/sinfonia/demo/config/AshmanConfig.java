package top.sinfonia.demo.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <br>
 * <b>Project:</b> spring-cloud-ribbon-demo<br>
 * <b>Date:</b> 2017/11/7 10:51<br>
 * <b>Author:</b> Asher<br>
 */
@Configuration
public class AshmanConfig {
    @Bean
    public IRule ribbonRule() {
        return new RandomRule();
    }
}
