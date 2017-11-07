package top.sinfonia.demo.ribbon.config;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import top.sinfonia.demo.config.AshmanConfig;
import top.sinfonia.demo.config.DemoserviceConfig;

/**
 * <br>
 * <b>Project:</b> spring-cloud-ribbon-demo<br>
 * <b>Date:</b> 2017/11/1 19:31<br>
 * <b>Author:</b> Asher<br>
 */
@Configuration
@RibbonClients(value = {
        @RibbonClient(name = "ashman", configuration = AshmanConfig.class),
        @RibbonClient(name = "demoservice", configuration = DemoserviceConfig.class)
})
public class RibbonConfig {
//    @Configuration
//    public class RibbonConfiguration {
//        @Bean
//        public IRule ribbonRule() {
//            return new RandomRule();
//        }
//    }
}
