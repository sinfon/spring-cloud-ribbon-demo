package top.sinfonia.demo.ribbon.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <br>
 * <b>Project:</b> spring-cloud-ribbon-demo<br>
 * <b>Date:</b> 2017/11/1 19:31<br>
 * <b>Author:</b> Asher<br>
 */
@Configuration
@RibbonClient(name = "spring-cloud-ribbon-demo", configuration = RibbonConfig.RibbonConfiguration.class)
public class RibbonConfig {
    @Configuration
    public class RibbonConfiguration {
        @Bean
        public IRule ribbonRule() {
            return new RandomRule();
        }
    }
}
