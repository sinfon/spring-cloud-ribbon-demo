package top.sinfonia.demo.ribbon.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <br>
 * <b>Project:</b> spring-cloud-ribbon-demo<br>
 * <b>Date:</b> 2017/11/1 19:32<br>
 * <b>Author:</b> Asher<br>
 */
@Configuration
public class AshmanRibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        return new RandomRule();
    }
}
